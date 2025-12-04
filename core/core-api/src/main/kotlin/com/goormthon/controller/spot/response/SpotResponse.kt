package com.goormthon.controller.spot.response

import com.goormthon.spot.Location
import com.goormthon.spot.Spot
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "장소 응답")
data class SpotResponse(
    @Schema(description = "장소 ID", example = "1")
    val id: Long,
    @Schema(description = "키워드", example = "cafe")
    val keyword: String,
    @Schema(description = "이름", example = "스타벅스")
    val name: String,
    @Schema(description = "설명", example = "편안한 분위기의 카페")
    val description: String,
    @Schema(description = "위치 정보")
    val location: Location,
    @Schema(description = "지도 링크", example = "https://maps.example.com/spot/1")
    val mapLink: String,
    @Schema(description = "팁", example = "오후 2시 이후가 한적해요")
    val tip: String,
    @Schema(description = "가중치", example = "0.85")
    val weight: Double = 0.0,
) {
    companion object {
        fun from(info: Spot.Info): SpotResponse =
            SpotResponse(
                id = info.id,
                keyword = info.keyword,
                name = info.name,
                description = info.description,
                location = info.location,
                mapLink = info.mapLink,
                tip = info.tip,
            )
    }
}
