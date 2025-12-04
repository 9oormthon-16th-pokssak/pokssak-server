package com.goormthon.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.goormthon.ai.client.GeminiChatClient
import com.goormthon.scheduler.dto.GeminiSpotResponse
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

    @Scheduled(cron = "* 0/10 * * * *")
    fun createSpotWithTagsJob() {
        try {
            val responseText = geminiChatClient.generateContent(
                prompt = """
                    너는 제주도 여행 전문가이자 백엔드 데이터 생성기야.
                    요청에 따라 제주도 여행 장소 데이터를 생성해서 JSON Array 형식으로만 응답해.

                    [필수 요구사항]
                    1. 다음 5가지 키워드에 맞춰 각 키워드 당 3개의 장소를 선정할 것 (총 15개).
                       - 키워드: "조용한", "로컬스러운", "활동적인", "자연적인", "많이 찾는"
                    2. 응답은 오직 JSON Array 문자열만 반환할 것. (Markdown 코드 블록 ```json ... ``` 없이 순수 텍스트로)
                    3. 중복된 장소가 없어야 함.
                    4. 설명(description)과 팁(tip)은 한국어로, 친절하고 구체적으로 작성.
                    5. 위경도(latitude, longitude)는 실제 구글맵 기준 데이터여야 함. 진짜 정확한 위경도를 넣어야해. 한번 검증을 진행해보고 넣는 것이 좋아.
                    6. "숨은 맛집", "찐맛집", "로컬 맛집", "도민 맛집", "제주도민 맛집", "현지인 맛집", "관광객 모르는 곳", "동네 사람들이 가는", "로컬 바이브" 해당 키워드 초점으로 큐레이션을 해줬으면 좋겠어.

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
                    }
                """.trimIndent(),
                contents = "제주도의 숨은 명소와 인기 명소를 섞어서, 위 5개 키워드별로 각각 5곳씩 추천해줘.",
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
            val existingSpots = spotService.findAllSpots().toMutableList()
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
