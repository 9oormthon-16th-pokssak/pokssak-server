package com.goormthon.user

import com.goormthon.enums.AuthorityType

data class User(
    val id: Long,
    val key: String,
    val role: AuthorityType,
)
