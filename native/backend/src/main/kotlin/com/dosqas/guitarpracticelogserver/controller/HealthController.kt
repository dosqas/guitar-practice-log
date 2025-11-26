package com.dosqas.guitarpracticelogserver.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {
    @GetMapping("/health")
    fun healthCheck(): Map<String, String> {
        return mapOf("status" to "UP")
    }
}
