package io.github.luishenriqueaguiar.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.data.repository.TimerStateRepository
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.model.SessionStatus
import io.github.luishenriqueaguiar.domain.model.TimerState
import io.github.luishenriqueaguiar.domain.usecase.GetSessionByIdUseCase
import io.github.luishenriqueaguiar.domain.usecase.UpdateSessionUseCase
import kotlinx.coroutines.*
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class FocusTimerService : Service() {

    @Inject lateinit var getSessionByIdUseCase: GetSessionByIdUseCase
    @Inject lateinit var updateSessionUseCase: UpdateSessionUseCase
    @Inject lateinit var timerStateRepository: TimerStateRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var timerJob: Job? = null
    private var currentSession: Session? = null
    private var remainingTimeInMillis: Long = 0L
    private var pauseStartTimeInMillis: Long = 0L

    private val binder = FocusTimerBinder()

    inner class FocusTimerBinder : Binder() {
        fun getService(): FocusTimerService = this@FocusTimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    companion object {
        const val ACTION_START = "ACTION_START"
        const val EXTRA_SESSION_ID = "EXTRA_SESSION_ID"
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "FocusTimerChannel"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sessionId = intent?.getStringExtra(EXTRA_SESSION_ID)
        sessionId?.let { setupSession(it) }
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Preparando sessão..."))
        return START_NOT_STICKY
    }

    private fun setupSession(sessionId: String) {
        serviceScope.launch {
            getSessionByIdUseCase(sessionId).onSuccess { session ->
                currentSession = session
                remainingTimeInMillis = if (session.actualStudyDurationInSeconds > 0 && session.status == SessionStatus.IN_PROGRESS) {
                    (session.plannedStudyDurationInMinutes * 60 * 1000L) - (session.actualStudyDurationInSeconds * 1000L)
                } else {
                    session.plannedStudyDurationInMinutes * 60 * 1000L
                }
                updateState(isPlaying = false)
            }
        }
    }

    fun startTimer() {
        if (timerJob?.isActive == true) return

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Iniciando..."))

        if (pauseStartTimeInMillis > 0L) {
            val pauseDuration = System.currentTimeMillis() - pauseStartTimeInMillis
            currentSession?.let {
                val updated = it.copy(totalPauseDurationInSeconds = it.totalPauseDurationInSeconds + (pauseDuration / 1000))
                currentSession = updated
            }
            pauseStartTimeInMillis = 0L
        }

        if (currentSession?.status == SessionStatus.PLANNED) {
            currentSession = currentSession?.copy(status = SessionStatus.IN_PROGRESS, startTime = Date())
            serviceScope.launch { updateSessionUseCase(currentSession!!) }
        }

        timerJob = serviceScope.launch {
            val timeOnPlay = remainingTimeInMillis
            val startTime = System.currentTimeMillis()
            while (remainingTimeInMillis > 0) {
                val elapsedTime = System.currentTimeMillis() - startTime
                remainingTimeInMillis = timeOnPlay - elapsedTime
                if (remainingTimeInMillis < 0) remainingTimeInMillis = 0
                updateState(isPlaying = true)
                delay(1000)
            }
            onSessionFinished()
        }
    }

    fun pauseTimer(isViolation: Boolean = false) {
        if (timerJob?.isActive == false && isPlaying() == false) return
        timerJob?.cancel()
        pauseStartTimeInMillis = System.currentTimeMillis()

        currentSession?.let {
            var updatedSession = it
            if (isViolation && it.status == SessionStatus.IN_PROGRESS) {
                updatedSession = it.copy(interruptionsCount = it.interruptionsCount + 1)
            }
            val studiedTime = (it.plannedStudyDurationInMinutes * 60 * 1000L - remainingTimeInMillis) / 1000
            updatedSession = updatedSession.copy(actualStudyDurationInSeconds = studiedTime)
            currentSession = updatedSession
            serviceScope.launch { updateSessionUseCase(updatedSession) }
        }
        updateState(isPlaying = false)
    }

    fun stopAndAbandonSession() {
        timerJob?.cancel()
        currentSession?.let {
            val studiedTime = (it.plannedStudyDurationInMinutes * 60 * 1000L - remainingTimeInMillis) / 1000
            val finalSession = it.copy(
                status = SessionStatus.ABANDONED,
                endTime = Date(),
                actualStudyDurationInSeconds = studiedTime
            )
            serviceScope.launch { updateSessionUseCase(finalSession) }
        }
        stopService()
    }

    private fun onSessionFinished() {
        timerJob?.cancel()
        currentSession?.let {
            val finalSession = it.copy(
                status = SessionStatus.COMPLETED,
                endTime = Date(),
                actualStudyDurationInSeconds = it.plannedStudyDurationInMinutes * 60L
            )
            serviceScope.launch { updateSessionUseCase(finalSession) }
        }
        vibrateLong()
        stopService()
    }

    private fun stopService() {
        timerStateRepository.resetState()
        stopForeground(true)
        stopSelf()
    }

    private fun updateState(isPlaying: Boolean) {
        val timeString = formatTime(remainingTimeInMillis)
        val totalDuration = currentSession?.plannedStudyDurationInMinutes?.times(60 * 1000L) ?: 1L
        val progress = (remainingTimeInMillis.toFloat() / totalDuration.toFloat() * 100).toInt()

        timerStateRepository.updateState(TimerState(timeString, progress, isPlaying))
        updateNotification(timeString)
    }

    private fun formatTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun isPlaying(): Boolean {
        return timerJob?.isActive ?: false
    }

    private fun createNotification(text: String): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Sessão de Foco Ativa")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_timer)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(text))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Focus Timer", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun vibrateLong() {
        val vibrator = getVibratorService()
        val pattern = longArrayOf(0, 500, 200, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    private fun getVibratorService(): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }
}