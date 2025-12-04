package com.goormthon.spot

class Spot {
    data class Create(
        val keyword: String,
        val name: String,
        val description: String,
        val location: Location,
        val mapLink: String,
        val tip: String,
    )

    data class Info(
        val id: Long,
        val keyword: String,
        val name: String,
        val description: String,
        val location: Location,
        val mapLink: String,
        val tip: String,
    )

    data class Detail(
        val id: Long,
        val keyword: String,
        val name: String,
        val description: String,
        val location: Location,
        val mapLink: String,
        val tip: String,
        val tags: List<String>,
    )
}
