# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ═══════════════════════════════════════════════════════════════════════════════
# RETROFIT
# ═══════════════════════════════════════════════════════════════════════════════
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# Keep Retrofit interfaces
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Keep generic type info for Retrofit
-keepattributes InnerClasses
-keep class retrofit2.** { *; }
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# ═══════════════════════════════════════════════════════════════════════════════
# OKHTTP
# ═══════════════════════════════════════════════════════════════════════════════
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ═══════════════════════════════════════════════════════════════════════════════
# KOTLINX SERIALIZATION
# ═══════════════════════════════════════════════════════════════════════════════
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep serializers
-keep,includedescriptorclasses class com.example.aroura.data.api.**$$serializer { *; }
-keepclassmembers class com.example.aroura.data.api.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.aroura.data.api.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep @Serializable classes
-keep @kotlinx.serialization.Serializable class com.example.aroura.** { *; }

# ═══════════════════════════════════════════════════════════════════════════════
# COMPOSE
# ═══════════════════════════════════════════════════════════════════════════════
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ═══════════════════════════════════════════════════════════════════════════════
# ENCRYPTED SHARED PREFERENCES
# ═══════════════════════════════════════════════════════════════════════════════
-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }

# ═══════════════════════════════════════════════════════════════════════════════
# COIL (Image Loading)
# ═══════════════════════════════════════════════════════════════════════════════
-keep class coil.** { *; }

# ═══════════════════════════════════════════════════════════════════════════════
# FACEBOOK SDK
# ═══════════════════════════════════════════════════════════════════════════════
-keep class com.facebook.** { *; }
-dontwarn com.facebook.**

# ═══════════════════════════════════════════════════════════════════════════════
# MEDIA3 / EXOPLAYER
# ═══════════════════════════════════════════════════════════════════════════════
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# ═══════════════════════════════════════════════════════════════════════════════
# GENERAL
# ═══════════════════════════════════════════════════════════════════════════════
# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile