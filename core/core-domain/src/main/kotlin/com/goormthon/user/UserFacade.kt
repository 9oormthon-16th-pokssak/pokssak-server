package com.goormthon.user

import com.goormthon.user.spot.UserSpotLikeService
import com.goormthon.user.spot.UserSpotVisitService
import org.springframework.stereotype.Service

@Service
class UserFacade(
    private val userService: UserService,
    private val userSpotLikeService: UserSpotLikeService,
    private val userSpotVisitService: UserSpotVisitService,
) {
    fun likeSpot(userId: Long, spotId: Long) =
        userSpotLikeService.likeSpot(userId, spotId)

    fun dislikeSpot(userId: Long, spotId: Long) =
        userSpotLikeService.dislikeSpot(userId, spotId)

    fun visitSpot(userId: Long, spotId: Long) =
        userSpotVisitService.visitSpot(userId, spotId)
}
