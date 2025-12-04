package com.goormthon.controller.auth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import com.goormthon.auth.AuthFacade
import com.goormthon.common.ApiResponse
import com.goormthon.controller.auth.request.AuthRequest
import com.goormthon.controller.auth.response.TokenResponse

@Tag(name = "Auth API", description = "인증 관련 API")
@RestController
class AuthController(
    private val authFacade: AuthFacade,
) {
    @Operation(summary = "이메일 회원가입", description = "이메일로 회원가입을 합니다.")
    @PostMapping("/signup")
    fun signup(
        @RequestBody @Valid request: AuthRequest.Signup,
    ): ApiResponse<TokenResponse> {
        val token = authFacade.signup(request.toCommand())

        return ApiResponse.success(
            HttpStatus.CREATED.value(),
            TokenResponse.from(token),
        )
    }

    @Operation(summary = "닉네임&키워드 회원가입 V2", description = "닉네임&키워드로 회원가입을 합니다.")
    @PostMapping("/v2/signup")
    fun signupV2(
        @RequestBody @Valid request: AuthRequest.SignupV2,
    ): ApiResponse<TokenResponse> {
        val token = authFacade.signupV2(request.toCommand())

        return ApiResponse.success(
            HttpStatus.CREATED.value(),
            TokenResponse.from(token),
        )
    }

    @Operation(summary = "로그인", description = "로그인을 합니다.")
    @PostMapping("/signin")
    fun signin(
        @RequestBody @Valid request: AuthRequest.Signin,
    ): TokenResponse =
        TokenResponse.from(authFacade.signin(request.toCommand()))
}
