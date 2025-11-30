package com.gameschat.app.data.repository

import com.gameschat.app.data.SupabaseClient
import com.gameschat.app.data.model.User
import io.github.jan.supabase.postgrest.from
import java.security.MessageDigest

class AuthRepository {
    private val client = SupabaseClient.client

    suspend fun signUp(username: String, password: String): Result<User> {
        return try {
            val existingUsers = client.from("users")
                .select {
                    filter {
                        eq("username", username)
                    }
                }
                .decodeList<User>()

            if (existingUsers.isNotEmpty()) {
                return Result.failure(Exception("Username already exists"))
            }

            val passwordHash = hashPassword(password)
            val newUser = User(username = username, passwordHash = passwordHash)
            
            val createdUser = client.from("users")
                .insert(newUser) {
                    select()
                }
                .decodeSingle<User>()

            Result.success(createdUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(username: String, password: String): Result<User> {
        return try {
            val passwordHash = hashPassword(password)
            val users = client.from("users")
                .select {
                    filter {
                        eq("username", username)
                        eq("password_hash", passwordHash)
                    }
                }
                .decodeList<User>()

            if (users.isEmpty()) {
                Result.failure(Exception("Invalid username or password"))
            } else {
                Result.success(users.first())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
