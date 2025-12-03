package com.goormthon.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import com.goormthon.common.ApiResponse
import com.goormthon.common.ErrorResponse
import com.goormthon.enums.ErrorType

class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authenticationException: AuthenticationException,
    ) {
        with(response) {
            status = HttpStatus.UNAUTHORIZED.value()
            contentType = MediaType.APPLICATION_JSON_VALUE
            characterEncoding = "UTF-8"
            writer.write(
                objectMapper.writeValueAsString(
                    ApiResponse.fail(
                        status = HttpStatus.UNAUTHORIZED.value(),
                        errorResponse =
                            ErrorResponse.of(
                                errorClassName = ErrorType.UNAUTHORIZED_TOKEN.name,
                                message = ErrorType.UNAUTHORIZED_TOKEN.message,
                            ),
                    ),
                ),
            )
        }
    }
}
