package com.goormthon.storage.rdb.user.spot

import com.goormthon.storage.rdb.support.BaseEntity
import com.goormthon.user.spot.UserSpotRecommend
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "t_user_spot_recommend")
class UserSpotRecommendEntity(
    val userId: Long,
    val spotId: Long,
    val weight: Double,
    val date: LocalDate,
) : BaseEntity() {
    constructor(
        create: UserSpotRecommend.Create,
    ) : this(
        userId = create.userId,
        spotId = create.spotId,
        weight = create.weight,
        date = create.date,
    )

    fun toInfo() = UserSpotRecommend.Info(
        id = id!!,
        userId = userId,
        spotId = spotId,
        weight = weight,
        date = date,
    )
}
