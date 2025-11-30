-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.gameschat.app.**$$serializer { *; }
-keepclassmembers class com.gameschat.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.gameschat.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep @kotlinx.serialization.Serializable class com.gameschat.app.** { *; }
-keep class com.gameschat.app.data.model.** { *; }
