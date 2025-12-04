package com.goormthon.spot

import com.goormthon.user.UserService
import com.goormthon.user.spot.UserSpotLikeService
import com.goormthon.user.spot.UserSpotRecommendService
import com.goormthon.user.spot.UserSpotVisitService
import java.time.LocalDate
import org.springframework.stereotype.Service

@Service
class SpotFacade(
    private val spotService: SpotService,
    private val userService: UserService,
    private val userSpotRecommendService: UserSpotRecommendService,
    private val userSpotLikeService: UserSpotLikeService,
    private val userSpotVisitService: UserSpotVisitService,
) {
    companion object {
        private const val DEFAULT_WEIGHT_STANDARD = 0.7
    }

    fun findDetail(spotId: Long, userId: Long): Spot.Detail {
        val spotDetail = spotService.findBy(spotId)

        // 유저가 로그인 한 경우에만 좋아요 및 방문 여부 체크
        val isLiked = userSpotLikeService.isUserLikedSpot(userId, spotId)
        val isVisited = userSpotVisitService.isUserVisitedSpot(userId, spotId)

        return Spot.Detail.of(
            spot = spotDetail.spot,
            tags = spotDetail.tags,
            isLiked = isLiked,
            isVisited = isVisited,
        )
    }

    fun findAllSpots(
        keyword: String?,
        swLat: Double?,
        swLng: Double?,
        neLat: Double?,
        neLng: Double?,
        userId: Long,
    ): List<Spot.Info> =
        spotService.findAllBy(
            keyword = keyword,
            swLat = swLat,
            swLng = swLng,
            neLat = neLat,
            neLng = neLng,
            userId = userId,
        )

    fun recommend(userId: Long): List<Spot.Info> {
        val recommends = userSpotRecommendService.findByUserIdAndToday(userId, LocalDate.now())
        if (recommends.isNotEmpty()) {
            val spotIds = recommends.map { it.spotId }
            return spotService.findAllByIds(spotIds)
        }

        val keyword = userService.getProfile(userId).keyword
        return spotService.findByKeyword(keyword)
            .asSequence()
            .filter { (it.weight ?: 0.0) >= DEFAULT_WEIGHT_STANDARD }
            .shuffled()
            .take(3)
            .toList()
    }
}
