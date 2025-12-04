package com.goormthon.controller.spot.response

import com.goormthon.spot.Location
import com.goormthon.spot.Spot
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "장소 상세 응답")
data class SpotDetailResponse(
    @Schema(description = "장소 ID", example = "1")
    val id: Long,
    @Schema(description = "장소 키워드", example = "키워드")
    val keyword: String,
    @Schema(description = "장소 이름", example = "이름")
    val name: String,
    @Schema(description = "장소 설명", example = "설명")
    val description: String,
    @Schema(description = "장소 위경도/주소")
    val location: Location,
    @Schema(description = "장소 지도 링크", example = "http://maplink.example.com")
    val mapLink: String,
    @Schema(description = "장소 팁", example = "팁")
    val tip: String,
    @Schema(description = "장소 태그 목록", example = "[\"태그1\", \"태그2\"]")
    val tags: List<String>,
    @Schema(description = "가중치", example = "0.0")
    val weight: Double? = 0.0,
    @Schema(description = "사용자가 좋아요를 눌렀는지 여부", example = "false")
    val isLiked: Boolean = false,
    @Schema(description = "사용자가 방문했는지 여부", example = "false")
    val isVisited: Boolean = false,
) {
    companion object {
        fun from(detail: Spot.Detail) =
            SpotDetailResponse(
                id = detail.id,
                keyword = detail.keyword,
                name = detail.name,
                description = detail.description,
                location = detail.location,
                mapLink = detail.mapLink,
                tip = detail.tip,
                tags = detail.tags,
                weight = detail.weight,
                isLiked = detail.isLiked,
                isVisited = detail.isVisited,
            )
    }
}
