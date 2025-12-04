package com.goormthon.user.spot

interface UserSpotLikeRepository {
    fun existsByUserIdAndSpotId(userId: Long, spotId: Long): Boolean
}
