package com.gameschat.app.data.repository

import com.gameschat.app.data.SupabaseClient
import com.gameschat.app.data.model.Message
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    fun subscribeToMessages(): Flow<Message> {
        val channel = client.channel("messages")
        val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "messages"
        }

        return changeFlow.map { action ->
            when (action) {
                is PostgresAction.Insert -> action.decodeRecord<Message>()
                is PostgresAction.Update -> action.decodeRecord<Message>()
                else -> null
            }
        }.map { it ?: Message("", "", "", "") }
    }

    suspend fun subscribeChannel() {
        val channel = client.channel("messages")
        channel.subscribe()
    }

    suspend fun unsubscribeChannel() {
        client.channel("messages").unsubscribe()
    }
}
