package com.goormthon.auth

import com.goormthon.enums.AuthorityType

data class GrantedAuthority(
    val authorityType: AuthorityType,
)
