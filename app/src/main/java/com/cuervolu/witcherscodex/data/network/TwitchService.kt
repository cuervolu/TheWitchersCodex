package com.cuervolu.witcherscodex.data.network

import com.cuervolu.witcherscodex.data.response.StreamsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface TwitchService {
    @GET
    suspend fun getStreamsByGame(@Url url:String): Response<StreamsResponse>
}