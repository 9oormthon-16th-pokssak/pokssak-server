package com.goormthon.user

data class UserCredential(
    val userId: Long,
    val userKey: String,
    val email: String?,
    val password: String?,
)
