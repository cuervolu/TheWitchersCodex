package com.cuervolu.witcherscodex.data.response

import com.google.gson.annotations.SerializedName

data class StreamsResponse(
    val data: List<Datum>,
    val pagination: Pagination
)

data class Datum(
    val id: String,
    @SerializedName("user_id") val userID: String,
    @SerializedName("user_login") val userLogin: String,
    @SerializedName("user_name") val userName: String,
    @SerializedName("game_id") val gameID: String,
    @SerializedName("game_name") val gameName: GameName,
    val type: Type,
    val title: String,
    @SerializedName("viewer_count") val viewerCount: Long,
    @SerializedName("started_at") val startedAt: String,
    val language: Language,
    @SerializedName("thumbnail_url") val thumbnailURL: String,
    @SerializedName("tag_ids") val tagIDS: List<Any?>,
    val tags: List<String>,
    @SerializedName("is_mature") val isMature: Boolean
)

enum class GameName {
    TheWitcher3WildHunt
}

enum class Language {
    En
}

enum class Type {
    Live
}

data class Pagination(
    val cursor: String
)

