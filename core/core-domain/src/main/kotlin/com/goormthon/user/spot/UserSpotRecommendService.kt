package com.goormthon.user.spot

import java.time.LocalDate
import org.springframework.stereotype.Service

@Service
class UserSpotRecommendService(
    private val userSpotRecommendRepository: UserSpotRecommendRepository,
) {
    fun findByUserIdAndToday(userId: Long, today: LocalDate): List<UserSpotRecommend.Info> {
        return userSpotRecommendRepository.findAllByUserIdAndToday(userId, today)
    }

    fun recommend(spots: List<UserSpotRecommend.Create>): List<UserSpotRecommend.Info> {
        return userSpotRecommendRepository.recommend(spots)
    }
}
