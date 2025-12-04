package com.goormthon.user.spot

interface UserSpotVisitRepository {
    fun existsByUserIdAndSpotId(userId: Long, spotId: Long): Boolean
}
