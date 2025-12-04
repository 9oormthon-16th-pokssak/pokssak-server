package com.goormthon.spot

data class FindSpotResult(
    val spot: Spot.Info,
    val tags: List<String>,
)
