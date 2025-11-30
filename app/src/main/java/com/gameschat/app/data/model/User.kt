package com.gameschat.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id")
    val id: String = "",
    @SerialName("username")
    val username: String,
    @SerialName("password_hash")
    val passwordHash: String,
    @SerialName("created_at")
    val createdAt: String = ""
)
