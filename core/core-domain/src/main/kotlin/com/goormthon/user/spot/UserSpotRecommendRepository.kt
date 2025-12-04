package com.goormthon.user.spot

import java.time.LocalDate

interface UserSpotRecommendRepository {
    fun recommend(spots: List<UserSpotRecommend.Create>): List<UserSpotRecommend.Info>
    fun findAllByUserIdAndToday(userId: Long, today: LocalDate): List<UserSpotRecommend.Info>
}
