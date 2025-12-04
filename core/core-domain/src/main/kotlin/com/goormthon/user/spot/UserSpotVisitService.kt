package com.goormthon.user.spot

import org.springframework.stereotype.Service

@Service
class UserSpotVisitService(
    private val userSpotVisitRepository: UserSpotVisitRepository,
) {
    fun isUserVisitedSpot(userId: Long, spotId: Long): Boolean =
        userSpotVisitRepository.existsByUserIdAndSpotId(userId, spotId)

    fun visitSpot(userId: Long, spotId: Long) =
        userSpotVisitRepository.visitSpot(userId, spotId)

    fun findAllByUserId(userId: Long): List<UserSpotVisit.Info> =
        userSpotVisitRepository.findAllByUserId(userId)
}
