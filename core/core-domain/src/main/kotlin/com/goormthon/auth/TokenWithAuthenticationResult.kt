package com.goormthon.auth

data class TokenWithAuthenticationResult(
    val accessToken: String,
    val refreshToken: String,
    val provider: Provider,
)
