package com.goormthon.controller.user.response

import io.swagger.v3.oas.annotations.media.Schema
import com.goormthon.support.page.Page
import com.goormthon.user.UserProfile

@Schema(description = "사용자 페이지 응답")
data class UserPageResponse(
    @Schema(description = "사용자 목록")
    val content: List<UserResponse.Profile>,
    @Schema(description = "전체 사용자 수")
    val totalCount: Long,
) {
    companion object {
        fun from(
            page: Page<UserProfile>,
        ): UserPageResponse = UserPageResponse(
            content = page.content.map(UserResponse.Profile::from),
            totalCount = page.totalCount,
        )
    }
}
