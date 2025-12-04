package com.goormthon.storage.rdb.user.spot

import java.time.LocalDate
import org.springframework.data.jpa.repository.JpaRepository

interface UserSpotRecommendJpaRepository : JpaRepository<UserSpotRecommendEntity, Long> {
    fun findAllByUserIdAndDate(userId: Long, today: LocalDate): List<UserSpotRecommendEntity>
}
