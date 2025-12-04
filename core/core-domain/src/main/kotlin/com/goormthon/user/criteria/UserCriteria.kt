package com.goormthon.user.criteria

import com.goormthon.enums.AuthorityType
import com.goormthon.user.command.UserCommand

sealed class UserCriteria {
    data class Create(
        val userKey: String,
        val name: String,
        val keyword: String,
        val email: String?,
        val password: String?,
        val role: AuthorityType = AuthorityType.USER,
    ) {
        companion object {
            fun of(userKey: String, command: UserCommand.Create) =
                Create(
                    userKey = userKey,
                    name = command.name,
                    email = command.email,
                    password = command.password,
                    keyword = "",
                )

            fun of(userKey: String, command: UserCommand.CreateV2) =
                Create(
                    userKey = userKey,
                    name = command.name,
                    keyword = command.keyword,
                    email = null,
                    password = null,
                )
        }
    }

    data class Update(
        val name: String?,
        val email: String?,
        val password: String?,
    ) {
        companion object {
            fun of(command: UserCommand.Update) =
                Update(
                    name = command.name,
                    email = command.email,
                    password = command.password,
                )
        }
    }
}
