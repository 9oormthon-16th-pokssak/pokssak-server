package com.goormthon.spot

class SpotTag {
    data class Create(
        val spotId: Long,
        val name: String,
    )

    data class Info(
        val id: Long,
        val spotId: Long,
        val name: String,
    )
}
