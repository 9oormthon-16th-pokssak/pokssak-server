package com.goormthon.controller.auth.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import com.goormthon.auth.command.AuthCommand
import com.goormthon.enums.Keyword

@Schema(description = "인증 요청")
sealed class AuthRequest {
    data class Signup(
        @Schema(description = "이름")
        @field:NotBlank
        val name: String,
        @Schema(description = "이메일")
        @field:NotBlank
        @field:Email
        val email: String,
        @Schema(description = "비밀번호")
        @field:NotBlank
        val password: String,
    ) {
        fun toCommand() =
            AuthCommand.SignUp(
                name = name,
                email = email,
                password = password,
            )
    }

    data class SignupV2(
        @Schema(description = "이름")
        @field:NotBlank
        val name: String,
        @Schema(description = "키워드")
        @field:NotBlank
        val keyword: Keyword,
    ) {
        fun toCommand() =
            AuthCommand.SignupV2(
                name = name,
                keyword = keyword.description,
            )
    }

    data class Signin(
        @Schema(description = "이메일")
        @field:NotBlank
        @field:Email
        val email: String,
        @Schema(description = "비밀번호")
        @field:NotBlank
        val password: String,
    ) {
        fun toCommand() =
            AuthCommand.SignIn(
                email = email,
                password = password,
            )
    }
}
