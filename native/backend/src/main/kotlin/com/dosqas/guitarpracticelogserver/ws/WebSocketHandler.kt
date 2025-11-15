package com.dosqas.guitarpracticelogserver.ws

import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.CopyOnWriteArrayList

@Component
class SessionWebSocketHandler : TextWebSocketHandler() {

    private val sessions = CopyOnWriteArrayList<WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
        println("Client connected: ${session.id}")
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // You can handle messages sent from clients if you want
        println("Message from client: ${message.payload}")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: org.springframework.web.socket.CloseStatus) {
        sessions.remove(session)
        println("Client disconnected: ${session.id}")
    }

    fun broadcast(message: String) {
        sessions.forEach {
            if (it.isOpen) it.sendMessage(TextMessage(message))
        }
    }
}
