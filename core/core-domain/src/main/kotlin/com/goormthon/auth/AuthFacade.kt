package com.goormthon.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import com.goormthon.auth.command.AuthCommand
import com.goormthon.enums.ErrorType
import com.goormthon.error.ErrorException
import com.goormthon.support.tx.Tx
import com.goormthon.user.UserService

@Service
class AuthFacade(
    private val authService: AuthService,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
) {
    fun signup(command: AuthCommand.SignUp): Token =
        Tx.writeable {
            userService.verifyEmail(command.email)
            val user = userService.createUser(command.toUserCommand(passwordEncoder.encode(command.password)))
            val token = authService.generateToken(AuthCommand.GenerateToken.toCommand(user))
            return@writeable token
        }

    fun signin(command: AuthCommand.SignIn): Token =
        Tx.writeable {
            val credential = userService.getCredentialByEmail(command.email)

            if (!passwordEncoder.matches(command.password, credential.password)) {
                throw ErrorException(ErrorType.INVALID_PASSWORD)
            }

            val token = authService.generateToken(AuthCommand.GenerateToken.toCommand(credential))
            return@writeable token
        }

    fun signupV2(command: AuthCommand.SignupV2): Token =
        Tx.writeable {
            val user = userService.createUserV2(command.toUserCommand())
            val token = authService.generateToken(AuthCommand.GenerateToken.toCommand(user))
            return@writeable token
        }
}
