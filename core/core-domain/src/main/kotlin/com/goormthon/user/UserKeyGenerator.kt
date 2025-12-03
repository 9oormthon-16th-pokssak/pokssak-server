package com.goormthon.user

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import org.springframework.stereotype.Component

@Component
class UserKeyGenerator {
    companion object {
        private val FORMAT_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

    fun generate() = "${generateDate()}_UK_${generateUUID()}"

    private fun generateUUID(): String = UUID.randomUUID().toString().replace("-", "")

    private fun generateDate(): String = FORMAT_YYYYMMDD.format(LocalDate.now())
}
