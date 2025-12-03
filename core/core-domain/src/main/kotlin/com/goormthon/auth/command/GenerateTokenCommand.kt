package com.goormthon.auth.command

import com.goormthon.auth.Token

data class GenerateTokenCommand(
    val token: Token,
)
