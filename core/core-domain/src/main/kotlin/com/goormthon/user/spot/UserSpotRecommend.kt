package com.goormthon.user.spot

import java.time.LocalDate

class UserSpotRecommend {
    data class Create(
        val userId: Long,
        val spotId: Long,
        val weight: Double,
        val date: LocalDate,
    )

    data class Info(
        val id: Long,
        val userId: Long,
        val spotId: Long,
        val weight: Double,
        val date: LocalDate,
    )
}
