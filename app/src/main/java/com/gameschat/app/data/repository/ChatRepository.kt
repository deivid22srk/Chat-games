package com.gameschat.app.data.repository

import com.gameschat.app.data.SupabaseClient
import com.gameschat.app.data.model.Message
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class ChatRepository {
    private val client = SupabaseClient.client

    suspend fun getMessages(): Result<List<Message>> {
        return try {
            val messages = client.from("messages")
                .select()
                .decodeList<Message>()
                .sortedBy { it.createdAt }
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(userId: String, username: String, content: String): Result<Message> {
        return try {
            val newMessage = Message(
                userId = userId,
                username = username,
                content = content
            )
            
            val sentMessage = client.from("messages")
                .insert(newMessage) {
                    select()
                }
                .decodeSingle<Message>()

            Result.success(sentMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun subscribeToMessages(): Flow<PostgresAction> {
        val channel = client.channel("messages")
        return channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "messages"
        }
    }

    suspend fun subscribeChannel() {
        val channel = client.channel("messages")
        channel.subscribe()
    }

    suspend fun unsubscribeChannel() {
        client.channel("messages").unsubscribe()
    }
}
