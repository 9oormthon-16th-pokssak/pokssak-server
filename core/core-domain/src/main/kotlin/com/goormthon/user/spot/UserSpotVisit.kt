package com.goormthon.user.spot

class UserSpotVisit {
    data class Create(
        val userId: Long,
        val spotId: Long,
    )

    data class Info(
        val id: Long,
        val userId: Long,
        val spotId: Long,
    )

    data class Delete(
        val userId: Long,
        val spotId: Long,
    )
}
