package com.goormthon.controller.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import com.goormthon.controller.user.request.UserRequest
import com.goormthon.controller.user.response.UserMessageResponse
import com.goormthon.controller.user.response.UserPageResponse
import com.goormthon.controller.user.response.UserResponse
import com.goormthon.support.page.OffsetPageRequest
import com.goormthon.user.User
import com.goormthon.user.UserFacade
import com.goormthon.user.UserService
import org.springframework.web.bind.annotation.PostMapping

@Tag(name = "User API", description = "사용자 관련 API")
@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val userFacade: UserFacade,
) {

    @Operation(summary = "어드민 사용자 조회(페이징)", description = "어드민 권한으로 사용자 목록을 조회합니다.")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getUsers(
        @Parameter(hidden = true) user: User,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): UserPageResponse =
        UserPageResponse.from(userService.getUsers(OffsetPageRequest(page, size)))

    @Operation(summary = "사용자 조회", description = "사용자 정보를 조회합니다.")
    @GetMapping("/{userId}")
    fun getUser(
        @Parameter(hidden = true) user: User,
        @PathVariable userId: Long,
    ): UserResponse.Profile =
        UserResponse.Profile.from(userService.getProfile(userId))

    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다.")
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or #userId == #user.id")
    fun modifyUser(
        @Parameter(hidden = true) user: User,
        @PathVariable userId: Long,
        @RequestBody request: UserRequest.Update,
    ): UserResponse.Profile =
        UserResponse.Profile.from(userService.modifyUser(userId, request.toCommand()))

    @Operation(summary = "사용자 삭제", description = "사용자 데이터를 삭제합니다.")
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or #userId == #user.id")
    fun deleteUser(
        @Parameter(hidden = true) user: User,
        @PathVariable userId: Long,
    ) = userService.deleteUser(userId)

    @Operation(summary = "장소 좋아요", description = "사용자가 장소를 좋아요합니다.")
    @PostMapping("/spot/like/{spotId}")
    fun likeSpot(
        @Parameter(hidden = true) user: User,
        @PathVariable spotId: Long,
    ): UserMessageResponse {
        userFacade.likeSpot(user.id, spotId)
        return UserMessageResponse("좋아요가 완료되었습니다.")
    }

    @Operation(summary = "장소 좋아요 취소", description = "사용자가 장소 좋아요를 취소합니다.")
    @DeleteMapping("/spot/like/{spotId}")
    fun dislikeSpot(
        @Parameter(hidden = true) user: User,
        @PathVariable spotId: Long,
    ): UserMessageResponse {
        userFacade.dislikeSpot(user.id, spotId)
        return UserMessageResponse("좋아요 취소가 완료되었습니다.")
    }

    @Operation(summary = "장소 방문", description = "사용자가 장소를 방문합니다.")
    @PostMapping("/spot/visit/{spotId}")
    fun visitSpot(
        @Parameter(hidden = true) user: User,
        @PathVariable spotId: Long,
    ): UserMessageResponse {
        userFacade.visitSpot(user.id, spotId)
        return UserMessageResponse("장소 방문이 기록되었습니다.")
    }
}
