package com.goormthon.storage.rdb.user.spot

import com.goormthon.user.spot.UserSpotRecommend
import com.goormthon.user.spot.UserSpotRecommendRepository
import java.time.LocalDate
import org.springframework.stereotype.Repository

@Repository
class UserSpotRecommendCoreRepository(
    private val userSpotRecommendJpaRepository: UserSpotRecommendJpaRepository,
) : UserSpotRecommendRepository {
    override fun recommend(spots: List<UserSpotRecommend.Create>): List<UserSpotRecommend.Info> {
        val entities = spots.map { UserSpotRecommendEntity(it) }
        val savedEntities = userSpotRecommendJpaRepository.saveAll(entities)
        return savedEntities.map { it.toInfo() }
    }

    override fun findAllByUserIdAndToday(userId: Long, today: LocalDate): List<UserSpotRecommend.Info> {
        val entities = userSpotRecommendJpaRepository.findAllByUserIdAndDate(userId, today)
        return entities.map { it.toInfo() }
    }
}
