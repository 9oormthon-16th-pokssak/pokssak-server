package com.goormthon.controller.spot

import com.goormthon.controller.spot.response.SpotAllResponse
import com.goormthon.controller.spot.response.SpotDetailResponse
import com.goormthon.enums.Keyword
import com.goormthon.spot.SpotFacade
import com.goormthon.user.User
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Spot API", description = "장소 관련 API")
@RestController
@RequestMapping("/spots")
class SpotController(
    private val spotFacade: SpotFacade,
) {
    @GetMapping
    fun findAllSpotBy(
        @RequestParam @Parameter(name = "keyword", description = "키워드") keyword: Keyword?,
        @RequestParam @Parameter(name = "swLat", description = "남서쪽 위도") swLat: Double?,
        @RequestParam @Parameter(name = "swLng", description = "남서쪽 경도") swLng: Double?,
        @RequestParam @Parameter(name = "neLat", description = "북동쪽 위도") neLat: Double?,
        @RequestParam @Parameter(name = "neLng", description = "북동쪽 경도") neLng: Double?,
        @Parameter(hidden = true) user: User,
    ): SpotAllResponse =
        SpotAllResponse.from(
            spotFacade.findAllSpots(
                keyword = keyword?.description,
                swLat = swLat,
                swLng = swLng,
                neLat = neLat,
                neLng = neLng,
                userId = user.id,
            ),
        )

    @GetMapping("/{spotId}")
    fun findSpotDetail(
        @PathVariable spotId: Long,
        @Parameter(hidden = true) user: User,
    ): SpotDetailResponse = SpotDetailResponse.from(spotFacade.findDetail(spotId, user.id))

    @PostMapping("/recommend")
    fun recommendSpot(
        @Parameter(hidden = true) user: User,
    ): SpotAllResponse = SpotAllResponse.from(spotFacade.recommend(user.id))
}
