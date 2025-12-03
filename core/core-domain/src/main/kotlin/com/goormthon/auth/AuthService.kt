package com.goormthon.auth

import org.springframework.stereotype.Service
import com.goormthon.auth.command.AuthCommand
import com.goormthon.auth.command.AuthenticationHistoryCommand
import com.goormthon.auth.command.GenerateTokenCommand
import com.goormthon.enums.TokenStatus

@Service
class AuthService(
    private val tokenRepository: TokenRepository,
    private val authenticationHistoryRepository: AuthenticationHistoryRepository,
) {
    fun generateToken(
        command: AuthCommand.GenerateToken,
    ) = tokenRepository.create(command.userId, command.userKey).apply {
        authenticationHistoryRepository.create(
            AuthenticationHistoryCommand.Create(
                userId = command.userId,
                userKey = command.userKey,
                command =
                    GenerateTokenCommand(
                        Token(
                            accessToken = this.accessToken,
                            refreshToken = this.refreshToken,
                        ),
                    ),
                status = TokenStatus.ACTIVE,
            ),
        )
    }
}
