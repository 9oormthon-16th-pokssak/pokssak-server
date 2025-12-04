package com.goormthon.controller.spot.response

import com.goormthon.spot.Spot
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "조회할 모든 장소 응답")
data class SpotAllResponse(
    @Schema(description = "장소 리스트")
    val list: List<SpotResponse>,
) {
    companion object {
        fun from(infos: List<Spot.Info>): SpotAllResponse =
            SpotAllResponse(
                list = infos.map { SpotResponse.from(it) },
            )
    }
}
