package com.goormthon.storage.rdb.spot

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SpotJpaRepository : JpaRepository<SpotEntity, Long> {
    @Query(
        """
        SELECT s FROM SpotEntity s
        WHERE (:keyword IS NULL OR s.keyword LIKE %:keyword%)
        AND (:swLat IS NULL OR s.location.latitude >= :swLat)
        AND (:neLat IS NULL OR s.location.latitude <= :neLat)
        AND (:swLng IS NULL OR s.location.longitude >= :swLng)
        AND (:neLng IS NULL OR s.location.longitude <= :neLng)
    """,
    )
    fun findAllBy(
        @Param("keyword") keyword: String?,
        @Param("swLat") swLat: Double?,
        @Param("swLng") swLng: Double?,
        @Param("neLat") neLat: Double?,
        @Param("neLng") neLng: Double?,
    ): List<SpotEntity>

    @Modifying
    @Query("UPDATE SpotEntity s SET s.weight = :weight WHERE s.id = :spotId")
    fun updateWeight(
        @Param("spotId") spotId: Long,
        @Param("weight") weight: Double,
    )

    fun findAllByIdInAndDeletedAtIsNull(spotIds: List<Long>): List<SpotEntity>
}
