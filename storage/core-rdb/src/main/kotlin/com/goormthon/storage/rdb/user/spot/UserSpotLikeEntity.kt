package com.goormthon.storage.rdb.user.spot

import com.goormthon.storage.rdb.support.BaseEntity
import com.goormthon.user.spot.UserSpotLike
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "t_user_spot_like",
    indexes = [
        Index(columnList = "user_id"),
        Index(columnList = "spot_id"),
    ],
)
class UserSpotLikeEntity(
    val userId: Long,
    val spotId: Long,
) : BaseEntity() {
    constructor(create: UserSpotLike.Create) : this(
        userId = create.userId,
        spotId = create.spotId,
    )

    fun toInfo() = UserSpotLike.Info(
        id = id!!,
        userId = userId,
        spotId = spotId,
    )
}
