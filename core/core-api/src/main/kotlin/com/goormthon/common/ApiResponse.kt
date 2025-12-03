package com.goormthon.common

import java.time.LocalDateTime

data class ApiResponse<Data>(
    val success: Boolean,
    val status: Int,
    val data: Data? = null,
    val timestamp: LocalDateTime,
) {
    companion object {
        fun <Data> success(
            status: Int,
            data: Data,
        ): ApiResponse<Data> =
            ApiResponse(true, status, data, LocalDateTime.now())

        fun fail(
            status: Int,
            errorResponse: com.goormthon.common.ErrorResponse,
        ): com.goormthon.common.ApiResponse<com.goormthon.common.ErrorResponse> =
            ApiResponse(false, status, errorResponse, LocalDateTime.now())
    }
}
