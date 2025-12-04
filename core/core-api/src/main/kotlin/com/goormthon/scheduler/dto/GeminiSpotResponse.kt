package com.goormthon.scheduler.dto

import com.goormthon.spot.Location
import com.goormthon.spot.Spot

data class GeminiSpotResponse(
    val keyword: String,
    val name: String,
    val description: String,
    val location: LocationDto,
    val mapLink: String,
    val tip: String,
    val tags: List<String>,
) {
    data class LocationDto(
        val latitude: Double,
        val longitude: Double,
        val address: String,
    ) {
        fun toDomain() = Location(
            latitude = latitude,
            longitude = longitude,
            address = address,
        )
    }

    fun toCreate() = Spot.Create(
        keyword = keyword,
        name = name,
        description = description,
        location = location.toDomain(),
        mapLink = mapLink,
        tip = tip,
    )
}
