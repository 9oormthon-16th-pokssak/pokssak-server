package com.goormthon.storage.rdb.spot

import com.goormthon.spot.SpotTag
import com.goormthon.storage.rdb.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "t_spot_tag",
    indexes = [
        Index(name = "idx_spot_id", columnList = "spot_id"),
    ],
)
class SpotTagEntity(
    val spotId: Long,
    val name: String,
) : BaseEntity() {
    constructor(
        create: SpotTag.Create,
    ) : this(
        spotId = create.spotId,
        name = create.name,
    )

    fun toSpotTag() =
        SpotTag.Info(
            id = id!!,
            spotId = spotId,
            name = name,
        )
}
