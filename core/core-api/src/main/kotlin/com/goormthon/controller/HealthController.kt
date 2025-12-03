package com.goormthon.controller

import io.swagger.v3.oas.annotations.Operation
import java.time.LocalDateTime
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {
    @GetMapping("/ping")
    @Operation(summary = "서버 상태 확인", description = "서버 상태를 확인합니다.")
    fun healthCheck(): PongResponse = PongResponse(LocalDateTime.now())

    data class PongResponse(
        val now: LocalDateTime,
    )
}
