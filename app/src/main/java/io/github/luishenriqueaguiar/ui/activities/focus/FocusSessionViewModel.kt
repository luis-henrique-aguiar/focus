package io.github.luishenriqueaguiar.ui.activities.focus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.model.FocusViolation
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.model.SessionStatus
import io.github.luishenriqueaguiar.domain.usecase.GetSessionByIdUseCase
import io.github.luishenriqueaguiar.domain.usecase.MonitorFocusUseCase
import io.github.luishenriqueaguiar.domain.usecase.UpdateSessionUseCase
import io.github.luishenriqueaguiar.ui.utils.OneTimeEvent
import io.github.luishenriqueaguiar.ui.utils.SessionEndEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FocusSessionViewModel @Inject constructor(
    private val getSessionByIdUseCase: GetSessionByIdUseCase,
    private val updateSessionUseCase: UpdateSessionUseCase,
    private val monitorFocusUseCase: MonitorFocusUseCase
) : ViewModel() {

    private val _session = MutableLiveData<Session>()
    val session: LiveData<Session> get() = _session

    private val _progress = MutableLiveData(100)
    val progress: LiveData<Int> get() = _progress

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _sessionFinishedEvent = MutableLiveData<Unit?>()
    val sessionFinishedEvent: LiveData<Unit?> get() = _sessionFinishedEvent

    private val _oneTimeEvent = MutableLiveData<OneTimeEvent?>()
    val oneTimeEvent: LiveData<OneTimeEvent?> get() = _oneTimeEvent

    private val _sessionEndEvent = MutableLiveData<SessionEndEvent?>()
    val sessionEndEvent: LiveData<SessionEndEvent?> get() = _sessionEndEvent

    private var lastStartTime: Long = 0L
    private var timerJob: Job? = null
    private var remainingTimeInMillis: Long = 0

    private val _timeDisplay = MutableLiveData("00:00")
    val timeDisplay: LiveData<String> get() = _timeDisplay

    private var pauseStartTimeInMillis: Long = 0L

    init {
        observeFocusViolations()
    }

    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            val result = getSessionByIdUseCase(sessionId)
            result.onSuccess { sessionData ->
                _session.value = sessionData
                remainingTimeInMillis = if (sessionData.actualStudyDurationInSeconds > 0) {
                    (sessionData.plannedStudyDurationInMinutes * 60L) - sessionData.actualStudyDurationInSeconds
                } else {
                    sessionData.plannedStudyDurationInMinutes * 60L
                } * 1000
                updateTimerDisplay(remainingTimeInMillis)
            }
        }
    }

    fun onPlayPauseClicked() {
        if (_isPlaying.value == true) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun onStopClicked() {
        _isPlaying.value = false
        timerJob?.cancel()
        updateSessionStatus(SessionStatus.ABANDONED, endTime = Date())
    }

    fun onResetClicked() {
        pauseTimer()
        val totalDuration = _session.value?.plannedStudyDurationInMinutes?.times(60 * 1000L) ?: 0L
        remainingTimeInMillis = totalDuration
        updateTimerDisplay(remainingTimeInMillis)
        updateProgressBar(remainingTimeInMillis, totalDuration)
    }

    fun onNavigationHandled() {
        _sessionFinishedEvent.value = null
    }

    fun onEventHandled() {
        _oneTimeEvent.value = null
    }

    fun onNavigationToSummaryHandled() {
        _sessionEndEvent.value = null
    }

    private fun observeFocusViolations() {
        viewModelScope.launch {
            monitorFocusUseCase.focusViolations.collect { violation ->
                when (violation) {
                    FocusViolation.PHONE_PLACED_FACE_DOWN -> {
                        if (_isPlaying.value == false) { startTimer() }
                    }
                    FocusViolation.PHONE_MOVED, FocusViolation.PHONE_FLIPPED_UP -> {
                        val timeSinceStart = System.currentTimeMillis() - lastStartTime
                        if (_isPlaying.value == true && timeSinceStart > 2000) {
                            pauseTimer()
                            _oneTimeEvent.value = OneTimeEvent.VibrateShort
                        }
                    }
                }
            }
        }
    }

    private fun startTimer() {
        _isPlaying.value = true
        lastStartTime = System.currentTimeMillis()

        if (pauseStartTimeInMillis > 0L) {
            val pauseDuration = System.currentTimeMillis() - pauseStartTimeInMillis
            val currentSession = _session.value
            currentSession?.let {
                val updatedSession = it.copy(
                    totalPauseDurationInSeconds = it.totalPauseDurationInSeconds + (pauseDuration / 1000)
                )
                _session.value = updatedSession
            }
            pauseStartTimeInMillis = 0L
        }

        if (_session.value?.status == SessionStatus.PLANNED) {
            updateSessionStatus(SessionStatus.IN_PROGRESS, startTime = Date())
        }

        timerJob = viewModelScope.launch {
            val timeOnPlay = remainingTimeInMillis
            val startTime = System.currentTimeMillis()
            val totalDuration = _session.value?.plannedStudyDurationInMinutes?.times(60 * 1000L) ?: 0L

            while (remainingTimeInMillis > 0) {
                val elapsedTimeSincePlay = System.currentTimeMillis() - startTime
                remainingTimeInMillis = timeOnPlay - elapsedTimeSincePlay
                if (remainingTimeInMillis < 0) remainingTimeInMillis = 0
                updateTimerDisplay(remainingTimeInMillis)
                updateProgressBar(remainingTimeInMillis, totalDuration)
                delay(1000)
            }
            onSessionFinished()
        }
    }

    private fun pauseTimer() {
        if (_isPlaying.value == false) return

        _isPlaying.value = false
        timerJob?.cancel()
        pauseStartTimeInMillis = System.currentTimeMillis()

        val currentSession = _session.value
        currentSession?.let {
            if (it.status == SessionStatus.IN_PROGRESS) {
                val updatedSession = it.copy(interruptionsCount = it.interruptionsCount + 1)
                _session.value = updatedSession
            }
        }

        updateSessionProgress()
    }

    private fun onSessionFinished() {
        _isPlaying.value = false
        _oneTimeEvent.value = OneTimeEvent.VibrateLong
        updateSessionStatus(SessionStatus.COMPLETED, endTime = Date())
    }

    private fun updateSessionProgress() {
        val currentSession = _session.value ?: return

        val updatedSession = currentSession.copy(
            actualStudyDurationInSeconds = (currentSession.plannedStudyDurationInMinutes * 60 * 1000L - remainingTimeInMillis) / 1000
        )

        viewModelScope.launch {
            updateSessionUseCase(updatedSession)
        }
    }

    private fun updateSessionStatus(newStatus: SessionStatus, startTime: Date? = _session.value?.startTime, endTime: Date? = null) {
        val currentSession = _session.value ?: return
        val updatedSession = currentSession.copy(
            status = newStatus,
            startTime = startTime,
            endTime = endTime,
            actualStudyDurationInSeconds = (currentSession.plannedStudyDurationInMinutes * 60 * 1000L - remainingTimeInMillis) / 1000
        )

        viewModelScope.launch {
            updateSessionUseCase(updatedSession)
            _session.value = updatedSession

            if (newStatus == SessionStatus.COMPLETED || newStatus == SessionStatus.ABANDONED) {
                _sessionEndEvent.value = SessionEndEvent.NavigateToSummary(updatedSession.id)
            }
        }
    }

    private fun updateTimerDisplay(timeInMillis: Long) {
        val minutes = (timeInMillis / 1000) / 60
        val seconds = (timeInMillis / 1000) % 60
        _timeDisplay.postValue(String.format("%02d:%02d", minutes, seconds))
    }

    private fun updateProgressBar(remainingMillis: Long, totalMillis: Long) {
        if (totalMillis > 0) {
            val progressPercent = (remainingMillis.toFloat() / totalMillis.toFloat() * 100).toInt()
            _progress.postValue(progressPercent)
        }
    }
}