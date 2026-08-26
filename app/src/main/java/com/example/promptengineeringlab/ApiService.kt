package com.example.promptengineeringlab

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("compare")
    suspend fun comparePrompts(
        @Body request: CompareRequest
    ): Response<CompareResponse>
}