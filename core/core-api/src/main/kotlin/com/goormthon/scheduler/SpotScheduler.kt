package com.goormthon.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.goormthon.ai.client.GeminiChatClient
import com.goormthon.scheduler.dto.GeminiSpotResponse
import com.goormthon.scheduler.dto.SpotWeightResponse
import com.goormthon.spot.SpotValidator
import com.goormthon.spot.SpotService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SpotScheduler(
    private val spotService: SpotService,
    private val geminiChatClient: GeminiChatClient,
    private val spotValidator: SpotValidator,
    private val objectMapper: ObjectMapper,
) {
    private val logger = KotlinLogging.logger {}

    // 기존 장소 데이터 가중치 재산정
    @Scheduled(cron = "0 0 */2 * * *")
    fun recalculateSpotWeightsJob() {
        try {
            val allSpots = spotService.findAllSpots()
            logger.info { "기존 장소 개수: ${allSpots.size}" }

            if (allSpots.isEmpty()) {
                logger.info { "재산정할 장소가 없습니다" }
                return
            }

            // Gemini에게 보낼 장소 정보 JSON 생성
            val spotsJson = allSpots.map { spot ->
                mapOf(
                    "id" to spot.id,
                    "name" to spot.name,
                    "keyword" to spot.keyword,
                    "description" to spot.description,
                    "address" to spot.location.address,
                )
            }
            val spotsJsonString = objectMapper.writeValueAsString(spotsJson)

            // Gemini에게 가중치 재산정 요청
            val responseText = geminiChatClient.generateContent(
                prompt = """
                    너는 제주도 여행 전문가야.
                    아래 제주도 장소들의 중요도와 인기도를 평가해서 각 장소마다 가중치(weight)를 부여해줘.

                    [가중치 평가 기준]
                    - 가중치는 0.0에서 1.0 사이의 값이야.
                    - 1.0에 가까울수록 더 중요하거나 인기 있는 장소야.
                    - 평가 기준:
                      1. 제주도 대표 명소 및 필수 방문지: 0.8~1.0
                      2. 인기 있는 명소 및 추천 장소: 0.6~0.8
                      3. 괜찮은 장소: 0.4~0.6
                      4. 숨은 명소 및 로컬 장소: 0.3~0.5
                      5. 덜 알려진 장소: 0.1~0.3

                    [응답 형식]
                    응답은 오직 JSON Array 문자열만 반환할 것. (Markdown 코드 블록 없이 순수 텍스트로)
                    각 객체는 다음 구조를 가져야 해:
                    {
                      "id": Long (장소 ID),
                      "weight": Double (0.0~1.0 사이의 가중치)
                    }
                """.trimIndent(),
                contents = spotsJsonString,
            )

            if (responseText == null) {
                logger.warn { "Gemini 응답이 null입니다" }
                return
            }

            logger.info { "Gemini 응답 받음: ${responseText.take(200)}..." }

            // Gemini 응답 파싱
            val cleanedJson = cleanJsonResponse(responseText)
            val weightResponses: List<SpotWeightResponse> = objectMapper.readValue(cleanedJson)

            logger.info { "파싱된 가중치 개수: ${weightResponses.size}" }

            // 각 장소의 가중치 업데이트
            var updatedCount = 0
            var failedCount = 0

            weightResponses.forEach { weightResponse ->
                try {
                    // 가중치 범위 검증 (0.0 ~ 1.0)
                    val validatedWeight = weightResponse.weight.coerceIn(0.0, 1.0)

                    spotService.updateSpotWeight(weightResponse.id, validatedWeight)

                    val spotName = allSpots.find { it.id == weightResponse.id }?.name ?: "Unknown"
                    logger.info { "장소 가중치 업데이트 완료: $spotName (ID: ${weightResponse.id}), 가중치: $validatedWeight" }
                    updatedCount++
                } catch (e: Exception) {
                    logger.error(e) { "장소 가중치 업데이트 실패: ID ${weightResponse.id}" }
                    failedCount++
                }
            }

            logger.info { "가중치 재산정 작업 완료 - 업데이트: ${updatedCount}개, 실패: ${failedCount}개" }
        } catch (e: Exception) {
            logger.error(e) { "가중치 재산정 작업 실패" }
        }
    }

    @Scheduled(cron = "* 0/30 * * * *")
    fun createSpotWithTagsJob() {
        try {
            val existingSpots = spotService.findAllSpots().toMutableList()
            val spotNames = existingSpots.map { it.name }.toSet()
            val responseText = geminiChatClient.generateContent(
                prompt = """
                    너는 제주도 여행 전문가이자 백엔드 데이터 생성기야.
                    요청에 따라 제주도 여행 장소 데이터를 생성해서 JSON Array 형식으로만 응답해.

                    [필수 요구사항]
                    1. 다음 5가지 키워드에 맞춰 각 키워드 당 3개의 장소를 선정할 것 (총 15개).
                       - 키워드: "조용한", "로컬스러운", "활동적인", "자연적인", "핫플레이스"
                    2. 응답은 오직 JSON Array 문자열만 반환할 것. (Markdown 코드 블록 ```json ... ``` 없이 순수 텍스트로)
                    3. 중복된 장소가 없어야 함.
                    4. 설명(description)과 팁(tip)은 한국어로, 친절하고 구체적으로 작성.
                    5. 위경도(latitude, longitude)는 실제 구글맵 기준 데이터여야 함. 진짜 정확한 위경도를 넣어야해. 한번 검증을 진행해보고 넣는 것이 좋아. 그리고, 무조건 장소 POINT로 해야하지, 전역적으로 포괄적인 좌표가 아니야.
                    6. 그리고 가중치(weight)라는 것이 있어. 가중치는 0.0에서 1.0 사이의 값으로, 장소의 중요도나 인기도를 나타내. 1.0에 가까울수록 더 중요하거나 인기 있는 장소야. 각 장소마다 적절한 가중치를 부여해줘.
                    7. (${spotNames.joinToString(", ")}) 이 장소들은 이미 데이터베이스에 있어. 이 장소들은 절대 포함하지 마.

                    [JSON 데이터 스키마]
                    응답은 아래 구조를 가진 객체들의 배열([])이어야 한다.
                    {
                      "keyword": "String (위 5개 키워드 중 하나)",
                      "name": "String (장소 이름)",
                      "description": "String (한 줄 소개)",
                      "location": {
                        "latitude": Double (위도),
                        "longitude": Double (경도),
                        "address": "String (도로명 주소)"
                      },
                      "mapLink": "String (구글맵 URL, ex. https://www.google.com/maps/search/?api=1&query=Biyangdo)",
                      "tip": "String (방문 꿀팁)",
                      "tags": ["String", "String"] (태그 리스트)
                      "weight": Double (0.0에서 1.0 사이의 가중치 값)
                    }
                """.trimIndent(),
                contents = "제주도의 숨은 명소와 인기 명소를 섞어서, 위 5개 키워드별로 각각 10곳씩 추천해줘.",
            )

            if (responseText == null) {
                logger.warn { "Gemini 응답이 null입니다" }
                return
            }

            logger.info { "Gemini 응답 받음: ${responseText.take(200)}..." }

            // Gemini 응답 파싱
            val cleanedJson = cleanJsonResponse(responseText)
            val spots: List<GeminiSpotResponse> = objectMapper.readValue(cleanedJson)

            logger.info { "파싱된 장소 개수: ${spots.size}" }

            // 성능 최적화: findAll()을 한 번만 호출하여 기존 Spot 목록 조회
            logger.info { "기존 장소 개수: ${existingSpots.size}" }

            // 각 장소 저장 (중복 검증 포함)
            var savedCount = 0
            var skippedCount = 0

            spots.forEach { geminiSpot: GeminiSpotResponse ->
                try {
                    val spotCreate = geminiSpot.toCreate()

                    // DDD Domain Service를 통한 중복 검증 (existingSpots 재사용)
                    val validationResult = spotValidator.validate(spotCreate, existingSpots)

                    if (!validationResult.valid) {
                        logger.warn {
                            "중복으로 인해 저장 건너뜀 - ${geminiSpot.name}, " +
                            "중복 필드: ${validationResult.duplicateFields.joinToString()}"
                        }
                        skippedCount++
                        return@forEach
                    }

                    // 중복이 없으면 저장
                    val savedSpot = spotService.createSpotWithTags(
                        create = spotCreate,
                        tagNames = geminiSpot.tags,
                    )

                    // 저장된 Spot을 existingSpots에 추가하여 다음 검증에 반영
                    existingSpots.add(savedSpot)

                    savedCount++
                    logger.info { "장소 저장 완료: ${geminiSpot.name}" }
                } catch (e: Exception) {
                    logger.error(e) { "장소 저장 실패: ${geminiSpot.name}" }
                }
            }

            logger.info { "스팟 생성 작업 완료 - 저장: ${savedCount}개, 중복 건너뜀: ${skippedCount}개" }
        } catch (e: Exception) {
            logger.error(e) { "스팟 생성 작업 실패" }
        }
    }

    /**
     * Gemini가 반환한 JSON에서 Markdown 코드 블록을 제거
     */
    private fun cleanJsonResponse(response: String): String {
        return response
            .replace("```json", "")
            .replace("```", "")
            .trim()
    }
}
