package com.goormthon.storage.rdb.spot

import org.springframework.data.jpa.repository.JpaRepository

interface SpotTagJpaRepository : JpaRepository<SpotTagEntity, Long> {
    fun findBySpotId(spotId: Long): List<SpotTagEntity>
}
