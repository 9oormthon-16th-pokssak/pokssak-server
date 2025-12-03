package com.goormthon.auth

import java.time.LocalDateTime
import com.goormthon.enums.TokenStatus

data class AuthenticationHistory(
    val authenticationId: Long,
    val userKey: String,
    val token: Token,
    val status: TokenStatus,
    val loggedInAt: LocalDateTime,
)
