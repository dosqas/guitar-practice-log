package com.dosqas.guitarpracticelog.data.remote

import com.dosqas.guitarpracticelog.data.model.PracticeSession
import retrofit2.Response
import retrofit2.http.*

interface PracticeApi {

    @GET("/sessions")
    suspend fun getAllSessions(): List<PracticeSession>

    @POST("/sessions")
    suspend fun createSession(@Body session: PracticeSessionRequestDto): PracticeSessionResponseDto

    @PUT("/sessions/{id}")
    suspend fun updateSession(@Path("id") id: Int, @Body session: PracticeSessionRequestDto): PracticeSessionResponseDto

    @DELETE("/sessions/{id}")
    suspend fun deleteSession(@Path("id") id: Int)

    @GET("/health")
    suspend fun pingServer(): Response<Unit>
}
