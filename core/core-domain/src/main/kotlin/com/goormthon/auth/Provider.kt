package com.goormthon.auth

data class Provider(
    val userId: Long,
    val userKey: String,
    val grantedAuthorities: List<String>,
)
