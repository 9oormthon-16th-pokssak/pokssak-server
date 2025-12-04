package com.goormthon.auth.command

import com.goormthon.user.User
import com.goormthon.user.UserCredential
import com.goormthon.user.command.UserCommand

class AuthCommand {
    data class SignIn(
        val email: String,
        val password: String,
    )

    data class SignUp(
        val name: String,
        val email: String,
        val password: String,
    ) {
        fun toUserCommand(encryptedPassword: String) =
            UserCommand.Create(
                name = name,
                email = email,
                password = encryptedPassword,
            )
    }

    data class SignupV2(
        val name: String,
        val keyword: String,
    ) {
        fun toUserCommand() =
            UserCommand.CreateV2(
                name = name,
                keyword = keyword,
            )
    }

    data class GenerateToken(
        val userId: Long,
        val userKey: String,
    ) {
        companion object {
            fun toCommand(user: User) =
                GenerateToken(
                    userId = user.id,
                    userKey = user.key,
                )

            fun toCommand(credential: UserCredential) =
                GenerateToken(
                    userId = credential.userId,
                    userKey = credential.userKey,
                )
        }
    }
}
