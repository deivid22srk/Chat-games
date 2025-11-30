package com.gameschat.app.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    private const val SUPABASE_URL = "https://uwauhtopwnzrofyeojbu.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV3YXVodG9wd256cm9meWVvamJ1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ1MzM5NDAsImV4cCI6MjA4MDEwOTk0MH0.AyHyeNPvzqc8tPw31o1HTGRRu7AEFaeBZLsXLLVunZo"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}
