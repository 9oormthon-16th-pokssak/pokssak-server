package com.goormthon.auth

data class Token(
    val accessToken: String,
    val refreshToken: String,
)
