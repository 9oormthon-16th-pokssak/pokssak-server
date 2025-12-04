package com.goormthon.storage.rdb.spot

import com.goormthon.spot.SpotTag
import com.goormthon.storage.rdb.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "t_spot_tag")
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
