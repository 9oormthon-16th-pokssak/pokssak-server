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
        val isLiked: Boolean = false,
        val isVisited: Boolean = false,
    ) {
        companion object {
            fun of(spot: Info, tags: List<String>, isLiked: Boolean, isVisited: Boolean): Detail =
                Detail(
                    id = spot.id,
                    keyword = spot.keyword,
                    name = spot.name,
                    description = spot.description,
                    location = spot.location,
                    mapLink = spot.mapLink,
                    tip = spot.tip,
                    tags = tags,
                    isLiked = isLiked,
                    isVisited = isVisited,
                )
        }
    }
}
