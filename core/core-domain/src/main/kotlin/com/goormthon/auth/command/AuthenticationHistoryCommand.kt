package com.goormthon.auth.command

import com.goormthon.enums.TokenStatus

sealed class AuthenticationHistoryCommand {
    data class Create(
        val userId: Long,
        val userKey: String,
        val command: GenerateTokenCommand,
        val status: TokenStatus,
    )

    data class Update(
        val userKey: String,
        val refreshToken: String,
        val command: GenerateTokenCommand,
    )
}
