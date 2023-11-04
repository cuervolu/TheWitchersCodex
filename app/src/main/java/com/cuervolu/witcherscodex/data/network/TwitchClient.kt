package com.cuervolu.witcherscodex.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TwitchClient {
    private fun getStreams(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.twitch.tv/helix/streams?/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}