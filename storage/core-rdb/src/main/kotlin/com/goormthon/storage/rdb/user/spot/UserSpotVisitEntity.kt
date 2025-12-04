package com.goormthon.storage.rdb.user.spot

import com.goormthon.storage.rdb.support.BaseEntity
import com.goormthon.user.spot.UserSpotVisit
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "t_user_spot_visit",
    indexes = [
        Index(columnList = "user_id"),
        Index(columnList = "spot_id"),
    ],
)
class UserSpotVisitEntity(
    val userId: Long,
    val spotId: Long,
) : BaseEntity() {
    constructor(
        create: UserSpotVisit.Create,
    ) : this(
        userId = create.userId,
        spotId = create.spotId,
    )

    fun toInfo() = UserSpotVisit.Info(
        id = id!!,
        userId = userId,
        spotId = spotId,
    )
}
