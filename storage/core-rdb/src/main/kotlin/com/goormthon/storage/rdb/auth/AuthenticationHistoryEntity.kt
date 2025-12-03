package com.goormthon.storage.rdb.auth

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import com.goormthon.auth.AuthenticationHistory
import com.goormthon.auth.Token
import com.goormthon.auth.command.AuthenticationHistoryCommand
import com.goormthon.enums.AuthenticationEntityStatus
import com.goormthon.enums.TokenStatus
import com.goormthon.storage.rdb.support.AuthenticationBaseEntity

@Entity
@Table(
    name = "t_authentication_history",
    indexes = [
        Index(name = "idx_authentication_history_user_id", columnList = "user_id"),
        Index(name = "idx_authentication_history_user_key", columnList = "user_key"),
        Index(name = "idx_authentication_history_access_token", columnList = "access_token"),
    ],
)
class AuthenticationHistoryEntity(
    val userId: Long,
    val userKey: String,
    @Column(columnDefinition = "TEXT")
    var accessToken: String,
    @Column(columnDefinition = "TEXT")
    var refreshToken: String,
) : AuthenticationBaseEntity() {
    constructor(
        create: AuthenticationHistoryCommand.Create,
    ) : this(
        userId = create.userId,
        userKey = create.userKey,
        accessToken = create.command.token.accessToken,
        refreshToken = create.command.token.refreshToken,
    )

    fun toAuthenticationHistory(): AuthenticationHistory =
        AuthenticationHistory(
            authenticationId = id!!,
            userKey = userKey,
            token =
                Token(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                ),
            status = entityStatus.toTokenStatus(),
            loggedInAt = updatedAt ?: createdAt,
        )

    fun updateRefreshToken(token: Token): AuthenticationHistory {
        this.accessToken = token.accessToken
        this.refreshToken = token.refreshToken
        return AuthenticationHistory(
            authenticationId = id!!,
            userKey = userKey,
            token =
                Token(
                    accessToken = token.accessToken,
                    refreshToken = token.refreshToken,
                ),
            status = entityStatus.toTokenStatus(),
            loggedInAt = updatedAt ?: createdAt,
        )
    }

    internal fun AuthenticationEntityStatus.toTokenStatus(): TokenStatus =
        when (this) {
            AuthenticationEntityStatus.ACTIVE -> com.goormthon.enums.TokenStatus.ACTIVE
            AuthenticationEntityStatus.DELETE -> com.goormthon.enums.TokenStatus.INACTIVE
        }
}
