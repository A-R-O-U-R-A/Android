package com.example.aroura.media

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "AudioPlayerManager"

/**
 * Audio Player Manager - Reliable Streaming Implementation
 * 
 * Features:
 * - Proper buffering configuration (10s pre-buffer, 10s post-buffer)
 * - Audio focus handling
 * - Stream redirect handling
 * - Error recovery with fallback URLs
 * - Loop support for ambient/nature sounds
 * - Sleep timer with fade out
 */
@OptIn(UnstableApi::class)
class AudioPlayerManager private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: AudioPlayerManager? = null
        
        // Buffer configuration (milliseconds)
        private const val MIN_BUFFER_MS = 10_000        // 10 seconds minimum buffer
        private const val MAX_BUFFER_MS = 60_000        // 60 seconds max buffer
        private const val PLAYBACK_BUFFER_MS = 10_000   // 10 seconds before playback starts
        private const val REBUFFER_MS = 5_000           // 5 seconds after rebuffer
        
        // Network configuration
        private const val CONNECT_TIMEOUT_MS = 15_000
        private const val READ_TIMEOUT_MS = 15_000
        private const val USER_AGENT = "AROURA-Android/1.0"
        
        fun getInstance(context: Context): AudioPlayerManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AudioPlayerManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // Audio focus handling
    private val audioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private var audioFocusRequest: AudioFocusRequest? = null
    private var hasAudioFocus = false
    
    // ExoPlayer with LAZY initialization - prevents blocking main thread on startup
    private val exoPlayer: ExoPlayer by lazy { createPlayer() }
    
    // Current track info for fallback
    private var currentTrackUrl: String? = null
    private var currentTrackBackupUrl: String? = null
    private var currentTrackTitle: String = ""
    private var currentTrackSubtitle: String = ""
    private var currentLoopEnabled: Boolean = false
    private var retryCount = 0
    private val maxRetries = 2
    
    // ═══════════════════════════════════════════════════════════════════════════
    // STATE
    // ═══════════════════════════════════════════════════════════════════════════
    
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _bufferingProgress = MutableStateFlow(0)
    val bufferingProgress: StateFlow<Int> = _bufferingProgress.asStateFlow()
    
    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    
    // ═══════════════════════════════════════════════════════════════════════════
    // PLAYER CREATION WITH CUSTOM BUFFERING
    // ═══════════════════════════════════════════════════════════════════════════
    
    private fun createPlayer(): ExoPlayer {
        // Custom load control for buffering
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                MIN_BUFFER_MS,           // minBufferMs
                MAX_BUFFER_MS,           // maxBufferMs
                PLAYBACK_BUFFER_MS,      // bufferForPlaybackMs
                REBUFFER_MS              // bufferForPlaybackAfterRebufferMs
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
        
        // HTTP data source with proper headers
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setConnectTimeoutMs(CONNECT_TIMEOUT_MS)
            .setReadTimeoutMs(READ_TIMEOUT_MS)
            .setUserAgent(USER_AGENT)
            .setAllowCrossProtocolRedirects(true)  // Important for redirects
        
        // Bandwidth meter for adaptive buffering
        val bandwidthMeter = DefaultBandwidthMeter.Builder(context)
            .setResetOnNetworkTypeChange(true)
            .build()
        
        // Data source factory that handles both HTTP and local files
        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
        
        // Media source factory
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
        
        return ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setBandwidthMeter(bandwidthMeter)
            .setMediaSourceFactory(mediaSourceFactory)
            .setHandleAudioBecomingNoisy(true)  // Pause when headphones disconnected
            .setWakeMode(C.WAKE_MODE_NONE)       // Disable wake mode to avoid permission issues
            .build()
            .apply {
                addListener(createPlayerListener())
            }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // PLAYER LISTENER
    // ═══════════════════════════════════════════════════════════════════════════
    
    private fun createPlayerListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            Log.d(TAG, "Playback state: ${playbackStateToString(state)}")
            
            when (state) {
                Player.STATE_IDLE -> {
                    _isLoading.value = false
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        isBuffering = false
                    )
                }
                Player.STATE_BUFFERING -> {
                    _isLoading.value = true
                    _playerState.value = _playerState.value.copy(isBuffering = true)
                    updateBufferingProgress()
                }
                Player.STATE_READY -> {
                    _isLoading.value = false
                    _playerState.value = _playerState.value.copy(
                        isBuffering = false,
                        hasError = false,
                        errorMessage = null
                    )
                    _duration.value = exoPlayer.duration.coerceAtLeast(0)
                    _bufferingProgress.value = 100
                    retryCount = 0  // Reset retry count on success
                    startProgressUpdates()
                }
                Player.STATE_ENDED -> {
                    _isLoading.value = false
                    if (_playerState.value.isLooping) {
                        exoPlayer.seekTo(0)
                        exoPlayer.play()
                    } else {
                        _playerState.value = _playerState.value.copy(isPlaying = false)
                        stopProgressUpdates()
                    }
                }
            }
        }
        
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
            if (isPlaying) {
                startProgressUpdates()
            } else {
                stopProgressUpdates()
            }
        }
        
        override fun onPlayerError(error: PlaybackException) {
            Log.e(TAG, "Player error: ${error.message}, code: ${error.errorCode}", error)
            
            // Try backup URL if available
            if (retryCount < maxRetries && currentTrackBackupUrl != null) {
                retryCount++
                Log.d(TAG, "Retrying with backup URL (attempt $retryCount)")
                playInternal(
                    url = currentTrackBackupUrl!!,
                    backupUrl = null,
                    title = currentTrackTitle,
                    subtitle = currentTrackSubtitle,
                    loop = currentLoopEnabled
                )
                return
            }
            
            _playerState.value = _playerState.value.copy(
                isPlaying = false,
                hasError = true,
                errorMessage = getReadableError(error)
            )
            _isLoading.value = false
            abandonAudioFocus()
        }
    }
    
    private fun playbackStateToString(state: Int): String = when (state) {
        Player.STATE_IDLE -> "IDLE"
        Player.STATE_BUFFERING -> "BUFFERING"
        Player.STATE_READY -> "READY"
        Player.STATE_ENDED -> "ENDED"
        else -> "UNKNOWN"
    }
    
    private fun getReadableError(error: PlaybackException): String {
        return when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> 
                "Network connection failed. Check your internet."
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> 
                "Connection timeout. Try again."
            PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> 
                "Audio not available (${error.message})"
            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> 
                "Audio file not found"
            PlaybackException.ERROR_CODE_IO_UNSPECIFIED -> 
                "Unable to load audio"
            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
            PlaybackException.ERROR_CODE_DECODING_FAILED -> 
                "Audio format not supported"
            else -> error.message ?: "Playback error"
        }
    }
    
    private fun updateBufferingProgress() {
        scope.launch {
            while (_playerState.value.isBuffering && isActive) {
                val buffered = exoPlayer.bufferedPercentage
                _bufferingProgress.value = buffered
                delay(200)
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // AUDIO FOCUS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private fun requestAudioFocus(): Boolean {
        if (hasAudioFocus) return true
        
        val focusRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setOnAudioFocusChangeListener { focusChange ->
                    handleAudioFocusChange(focusChange)
                }
                .build()
                .also { audioFocusRequest = it }
        } else {
            null
        }
        
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null) {
            audioManager.requestAudioFocus(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                { focusChange -> handleAudioFocusChange(focusChange) },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        
        hasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        return hasAudioFocus
    }
    
    private fun abandonAudioFocus() {
        if (!hasAudioFocus) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
        hasAudioFocus = false
    }
    
    private fun handleAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                exoPlayer.volume = 1f
                if (_playerState.value.wasPlayingBeforeFocusLoss) {
                    resume()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                _playerState.value = _playerState.value.copy(
                    wasPlayingBeforeFocusLoss = exoPlayer.isPlaying
                )
                pause()
                hasAudioFocus = false
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                _playerState.value = _playerState.value.copy(
                    wasPlayingBeforeFocusLoss = exoPlayer.isPlaying
                )
                pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                exoPlayer.volume = 0.3f
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // PLAYBACK CONTROL
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Play audio from URL with optional backup URL
     */
    fun play(
        url: String,
        backupUrl: String? = null,
        title: String = "",
        subtitle: String = "",
        loop: Boolean = false
    ) {
        retryCount = 0
        playInternal(url, backupUrl, title, subtitle, loop)
    }
    
    private fun playInternal(
        url: String,
        backupUrl: String?,
        title: String,
        subtitle: String,
        loop: Boolean
    ) {
        try {
            Log.d(TAG, "Playing: $url")
            
            // Store for potential retry
            currentTrackUrl = url
            currentTrackBackupUrl = backupUrl
            currentTrackTitle = title
            currentTrackSubtitle = subtitle
            currentLoopEnabled = loop
            
            // Request audio focus
            if (!requestAudioFocus()) {
                Log.w(TAG, "Failed to get audio focus")
            }
            
            // Stop current playback
            exoPlayer.stop()
            _bufferingProgress.value = 0
            _isLoading.value = true
            
            // Create media item
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(url))
                .setTag(AudioMetadata(title, subtitle))
                .build()
            
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            exoPlayer.playWhenReady = true
            exoPlayer.prepare()
            
            _playerState.value = _playerState.value.copy(
                currentUrl = url,
                currentTitle = title,
                currentSubtitle = subtitle,
                isLooping = loop,
                hasError = false,
                errorMessage = null
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio", e)
            _playerState.value = _playerState.value.copy(
                hasError = true,
                errorMessage = e.message ?: "Failed to play audio"
            )
            _isLoading.value = false
        }
    }
    
    fun resume() {
        if (!requestAudioFocus()) return
        exoPlayer.play()
    }
    
    fun pause() {
        exoPlayer.pause()
    }
    
    fun togglePlayPause() {
        if (exoPlayer.isPlaying) pause() else resume()
    }
    
    fun stop() {
        exoPlayer.stop()
        _currentPosition.value = 0
        _playerState.value = PlayerState()
        cancelSleepTimer()
        abandonAudioFocus()
    }
    
    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs.coerceIn(0, _duration.value))
    }
    
    fun seekForward(ms: Long = 10000) {
        val newPosition = (_currentPosition.value + ms).coerceIn(0, _duration.value)
        seekTo(newPosition)
    }
    
    fun seekBackward(ms: Long = 10000) {
        val newPosition = (_currentPosition.value - ms).coerceIn(0, _duration.value)
        seekTo(newPosition)
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // LOOP CONTROL
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun toggleLoop() {
        val newLoopState = !_playerState.value.isLooping
        exoPlayer.repeatMode = if (newLoopState) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        _playerState.value = _playerState.value.copy(isLooping = newLoopState)
        currentLoopEnabled = newLoopState
    }
    
    fun setLoop(enabled: Boolean) {
        exoPlayer.repeatMode = if (enabled) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        _playerState.value = _playerState.value.copy(isLooping = enabled)
        currentLoopEnabled = enabled
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // VOLUME CONTROL
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun setVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0f, 1f)
        exoPlayer.volume = clampedVolume
        _playerState.value = _playerState.value.copy(volume = clampedVolume)
    }
    
    fun getVolume(): Float = exoPlayer.volume
    
    // ═══════════════════════════════════════════════════════════════════════════
    // SLEEP TIMER
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun setSleepTimer(minutes: Int) {
        cancelSleepTimer()
        
        if (minutes <= 0) {
            _playerState.value = _playerState.value.copy(
                sleepTimerEnabled = false,
                sleepTimerMinutesRemaining = 0
            )
            return
        }
        
        _playerState.value = _playerState.value.copy(
            sleepTimerEnabled = true,
            sleepTimerMinutesRemaining = minutes
        )
        
        sleepTimerJob = scope.launch {
            var remainingMinutes = minutes
            while (remainingMinutes > 0 && isActive) {
                delay(60000)
                remainingMinutes--
                _playerState.value = _playerState.value.copy(
                    sleepTimerMinutesRemaining = remainingMinutes
                )
            }
            
            if (remainingMinutes <= 0) {
                fadeOutAndStop()
            }
        }
    }
    
    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _playerState.value = _playerState.value.copy(
            sleepTimerEnabled = false,
            sleepTimerMinutesRemaining = 0
        )
    }
    
    private suspend fun fadeOutAndStop() {
        val currentVolume = exoPlayer.volume
        val steps = 20
        val stepDelay = 100L
        
        for (i in steps downTo 0) {
            exoPlayer.volume = currentVolume * (i.toFloat() / steps)
            delay(stepDelay)
        }
        
        stop()
        exoPlayer.volume = currentVolume
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // PROGRESS UPDATES
    // ═══════════════════════════════════════════════════════════════════════════
    
    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressJob = scope.launch {
            while (isActive && (exoPlayer.isPlaying || exoPlayer.playbackState == Player.STATE_BUFFERING)) {
                _currentPosition.value = exoPlayer.currentPosition.coerceAtLeast(0)
                delay(500)
            }
        }
    }
    
    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // UTILITY
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun isPlaying(): Boolean = exoPlayer.isPlaying
    
    fun release() {
        stopProgressUpdates()
        cancelSleepTimer()
        abandonAudioFocus()
        exoPlayer.release()
        INSTANCE = null
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ═══════════════════════════════════════════════════════════════════════════════

data class PlayerState(
    val isPlaying: Boolean = false,
    val isLooping: Boolean = false,
    val isBuffering: Boolean = false,
    val volume: Float = 1f,
    val currentUrl: String? = null,
    val currentTitle: String = "",
    val currentSubtitle: String = "",
    val sleepTimerEnabled: Boolean = false,
    val sleepTimerMinutesRemaining: Int = 0,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val wasPlayingBeforeFocusLoss: Boolean = false
)

data class AudioMetadata(
    val title: String,
    val subtitle: String
)

// ═══════════════════════════════════════════════════════════════════════════════
// EXTENSION FUNCTIONS
// ═══════════════════════════════════════════════════════════════════════════════

fun Long.formatDuration(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}

fun Long.formatRemainingDuration(total: Long): String {
    val remaining = total - this
    return "-${remaining.formatDuration()}"
}
