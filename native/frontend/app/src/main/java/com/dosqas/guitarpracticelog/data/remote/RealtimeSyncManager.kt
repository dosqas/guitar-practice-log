package com.dosqas.guitarpracticelog.data.remote

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.dosqas.guitarpracticelog.data.db.PracticeDao
import com.dosqas.guitarpracticelog.data.model.PracticeSession
import com.dosqas.guitarpracticelog.data.model.SyncStatus
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import kotlin.math.pow

data class WsMessage(
    val type: String,
    val payload: JsonElement
)

data class DeletePayload(
    val id: Int
)

class RealtimeSyncManager(
    private val client: OkHttpClient,
    private val dao: PracticeDao,
    private val gson: Gson
) {
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var reconnectAttempts = 0

    private val wsUrl = "ws://10.0.2.2:8080/ws"

    init {
        Log.d(TAG, "Initializing RealtimeSyncManager for sessions…")
        connect()
    }

    private fun connect() {
        if (webSocket != null) {
            Log.d(TAG, "Already connected to WebSocket.")
            return
        }

        Log.d(TAG, "Connecting to WebSocket: $wsUrl")

        val request = Request.Builder().url(wsUrl).build()

        val wsClient = client.newBuilder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .pingInterval(30, TimeUnit.SECONDS)
            .build()

        webSocket = wsClient.newWebSocket(request, SessionWebSocketListener())
    }

    private fun scheduleReconnect() {
        val backoffMillis = (INITIAL_RECONNECT_DELAY * 2.0.pow(reconnectAttempts.toDouble())).toLong()
            .coerceAtMost(MAX_RECONNECT_DELAY)

        Log.d(TAG, "Scheduling reconnect in $backoffMillis ms (Attempt ${reconnectAttempts + 1})")

        webSocket = null

        Handler(Looper.getMainLooper()).postDelayed({
            reconnectAttempts++
            connect()
        }, backoffMillis)
    }

    inner class SessionWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.i(TAG, "WebSocket connected!")
            reconnectAttempts = 0
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "WebSocket message received: $text")
            try {
                val message = gson.fromJson(text, WsMessage::class.java)

                scope.launch {
                    handleWsMessage(message)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse WebSocket message", e)
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.w(TAG, "WebSocket Closing: $code / $reason")
            webSocket.close(1000, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.w(TAG, "WebSocket Closed: $code / $reason")
            scheduleReconnect()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket Failure: ${t.message}", t)
            scheduleReconnect()
        }
    }

    private suspend fun handleWsMessage(message: WsMessage) {
        when (message.type) {

            // 🔵 Server pushed NEW session
            "SESSION_ADDED" -> {
                val session = gson.fromJson(message.payload, PracticeSession::class.java)
                dao.insertSession(session.copy(status = SyncStatus.SYNCED))
                Log.i(TAG, "Real-time ADD: ${session.songTitle}")
            }

            // 🔵 Server pushed UPDATED session
            "SESSION_UPDATED" -> {
                val session = gson.fromJson(message.payload, PracticeSession::class.java)
                dao.updateSession(session.copy(status = SyncStatus.SYNCED))
                Log.i(TAG, "Real-time UPDATE: ${session.songTitle}")
            }

            // 🔴 Server pushed DELETE
            "SESSION_DELETED" -> {
                val id = message.payload.asInt
                val session = dao.getSessionById(id)
                if (session != null) {
                    dao.deleteSession(id)
                    Log.i(TAG, "Real-time DELETE: ID $id")
                }
            }

            else -> {
                Log.w(TAG, "Unknown WebSocket message type: ${message.type}")
            }
        }
    }

    companion object {
        private const val TAG = "RealtimeSyncManager"
        private const val INITIAL_RECONNECT_DELAY = 1000L
        private const val MAX_RECONNECT_DELAY = 60000L
    }
}
