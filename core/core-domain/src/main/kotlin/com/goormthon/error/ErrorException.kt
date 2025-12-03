package com.goormthon.error

import com.goormthon.enums.ErrorType

data class ErrorException(
    val errorType: ErrorType,
    val data: Any? = null,
) : RuntimeException(errorType.message)
