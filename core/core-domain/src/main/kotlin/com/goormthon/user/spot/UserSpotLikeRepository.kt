package com.goormthon.user.spot

interface UserSpotLikeRepository {
    fun existsByUserIdAndSpotId(userId: Long, spotId: Long): Boolean
    fun likeSpot(userId: Long, spotId: Long)
    fun dislikeSpot(userId: Long, spotId: Long)
}
