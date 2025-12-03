package com.goormthon.config

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import java.lang.reflect.Method
import com.goormthon.enums.ErrorLevel
import com.goormthon.error.ErrorException
import com.goormthon.support.logging.logger

class AsyncExceptionHandler : AsyncUncaughtExceptionHandler {
    private val logger by logger()

    override fun handleUncaughtException(
        e: Throwable,
        method: Method,
        vararg params: Any?,
    ) {
        if (e is ErrorException) {
            when (e.errorType.level) {
                ErrorLevel.ERROR -> logger.error { "${"ErrorException : {}"} ${e.message} $e" }
                ErrorLevel.WARN -> logger.warn { "${"ErrorException : {}"} ${e.message} $e" }
                else -> logger.info { "${"ErrorException : {}"} ${e.message} $e" }
            }
        } else {
            logger.error { "${"Exception : {}"} ${e.message} $e" }
        }
    }
}
