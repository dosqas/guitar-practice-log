package com.dosqas.guitarpracticelogserver.config

import com.dosqas.guitarpracticelogserver.ws.SessionWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val sessionWebSocketHandler: SessionWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(sessionWebSocketHandler, "/ws")
            .setAllowedOrigins("*") // allow all for testing
    }
}
