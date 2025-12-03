package com.goormthon.auth

import com.goormthon.auth.command.AuthenticationHistoryCommand

interface AuthenticationHistoryRepository {
    fun create(authenticationHistoryCommand: AuthenticationHistoryCommand.Create): AuthenticationHistory
    fun findByUserKeyWithRefreshToken(userKey: String, refreshToken: String): AuthenticationHistory
    fun update(command: AuthenticationHistoryCommand.Update): AuthenticationHistory?
    fun remove(token: String): String
    fun removeToken(userKey: String): List<String>
}
