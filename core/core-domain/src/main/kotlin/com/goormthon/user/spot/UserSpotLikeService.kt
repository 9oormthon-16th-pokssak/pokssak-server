package com.goormthon.user.spot

import org.springframework.stereotype.Service

@Service
class UserSpotLikeService(
    private val userSpotLikeRepository: UserSpotLikeRepository,
) {
    fun isUserLikedSpot(userId: Long, spotId: Long): Boolean =
        userSpotLikeRepository.existsByUserIdAndSpotId(userId, spotId)

    fun likeSpot(userId: Long, spotId: Long) =
        userSpotLikeRepository.likeSpot(userId, spotId)

    fun dislikeSpot(userId: Long, spotId: Long) =
        userSpotLikeRepository.dislikeSpot(userId, spotId)
}
