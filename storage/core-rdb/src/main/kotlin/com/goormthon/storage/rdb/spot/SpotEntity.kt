package com.goormthon.storage.rdb.spot

import com.goormthon.spot.Location
import com.goormthon.spot.Spot
import com.goormthon.storage.rdb.support.BaseEntity
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "t_spot")
class SpotEntity(
    val keyword: String,
    val name: String,
    val description: String,
    @Embedded
    val location: Location,
    val mapLink: String,
    val tip: String,
) : BaseEntity() {
    constructor(
        create: Spot.Create,
    ) : this(
        keyword = create.keyword,
        name = create.name,
        description = create.description,
        location = create.location,
        mapLink = create.mapLink,
        tip = create.tip,
    )

    fun toInfo() =
        Spot.Info(
            id = id!!,
            keyword = keyword,
            name = name,
            description = description,
            location = location,
            mapLink = mapLink,
            tip = tip,
        )
}
