package com.gameschat.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("id")
    val id: String = "",
    @SerialName("user_id")
    val userId: String,
    @SerialName("username")
    val username: String,
    @SerialName("content")
    val content: String,
    @SerialName("created_at")
    val createdAt: String = ""
)
