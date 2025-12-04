package com.goormthon.spot

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

/**
 * DDD Domain Service: Spot 중복 검증 컴포넌트
 * - name, mapLink, location에 대한 중복 여부를 검증
 */
@Component
class SpotValidator(
    private val spotRepository: SpotRepository,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Spot 생성 전 중복 검증 (DB 조회 포함)
     * @param create 생성할 Spot 정보
     * @return 중복 검증 결과 (중복이 없으면 valid = true)
     */
    fun validate(create: Spot.Create): DuplicateCheckResult {
        val existingSpots = spotRepository.findAll()
        return validate(create, existingSpots)
    }

    /**
     * Spot 생성 전 중복 검증 (기존 Spot 리스트 재사용)
     * 성능 최적화: 여러 개의 Spot을 검증할 때 findAll()을 한 번만 호출하고 재사용
     * @param create 생성할 Spot 정보
     * @param existingSpots 기존 Spot 리스트
     * @return 중복 검증 결과 (중복이 없으면 valid = true)
     */
    fun validate(create: Spot.Create, existingSpots: List<Spot.Info>): DuplicateCheckResult {
        val duplicateFields = mutableListOf<String>()

        // name 중복 체크
        if (existingSpots.any { it.name == create.name }) {
            duplicateFields.add("name: ${create.name}")
        }

        // mapLink 중복 체크
        if (existingSpots.any { it.mapLink == create.mapLink }) {
            duplicateFields.add("mapLink: ${create.mapLink}")
        }

        // location 중복 체크 (latitude, longitude, address 모두 같은 경우)
        if (existingSpots.any {
                it.location.latitude == create.location.latitude &&
                    it.location.longitude == create.location.longitude &&
                    it.location.address == create.location.address
            }
        ) {
            duplicateFields.add(
                "location: (${create.location.latitude}, ${create.location.longitude}, ${create.location.address})",
            )
        }

        val result = DuplicateCheckResult(
            valid = duplicateFields.isEmpty(),
            duplicateFields = duplicateFields,
        )

        if (!result.valid) {
            logger.warn { "중복 검증 실패 - Spot: ${create.name}, 중복 필드: ${duplicateFields.joinToString()}" }
        }

        return result
    }

    /**
     * 중복 검증 결과
     * @param valid 중복이 없으면 true
     * @param duplicateFields 중복된 필드 목록 (예: ["name: 성산일출봉", "mapLink: https://..."])
     */
    data class DuplicateCheckResult(
        val valid: Boolean,
        val duplicateFields: List<String>,
    )
}
