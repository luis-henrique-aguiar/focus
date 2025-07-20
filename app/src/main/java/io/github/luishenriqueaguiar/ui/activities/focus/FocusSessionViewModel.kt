package io.github.luishenriqueaguiar.ui.activities.focus

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.data.repository.TimerStateRepository
import io.github.luishenriqueaguiar.domain.model.FocusViolation
import io.github.luishenriqueaguiar.domain.model.SessionStatus
import io.github.luishenriqueaguiar.domain.usecase.GetSessionFlowUseCase
import io.github.luishenriqueaguiar.domain.usecase.MonitorFocusUseCase
import io.github.luishenriqueaguiar.services.FocusTimerService
import io.github.luishenriqueaguiar.ui.utils.OneTimeEvent
import io.github.luishenriqueaguiar.ui.utils.SessionEndEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FocusSessionViewModel @Inject constructor(
    private val monitorFocusUseCase: MonitorFocusUseCase,
    private val getSessionFlowUseCase: GetSessionFlowUseCase,
    timerStateRepository: TimerStateRepository
) : ViewModel() {

    val timerState = timerStateRepository.timerState.asLiveData()
    private val _sessionName = MutableLiveData<String>()
    val sessionName: LiveData<String> get() = _sessionName
    private val _oneTimeEvent = MutableLiveData<OneTimeEvent?>()
    val oneTimeEvent: LiveData<OneTimeEvent?> get() = _oneTimeEvent
    private val _sessionEndEvent = MutableLiveData<SessionEndEvent?>()
    val sessionEndEvent: LiveData<SessionEndEvent?> get() = _sessionEndEvent
    private var service: FocusTimerService? = null
    private var lastStartTime: Long = 0L

    init {
        observeFocusViolations()
    }

    fun setService(service: FocusTimerService?) {
        this.service = service
    }

    fun onPlayPauseClicked() {
        if (timerState.value?.isPlaying == true) {
            service?.pauseTimer()
        } else {
            service?.startTimer()
        }
    }

    fun loadAndObserveSession(sessionId: String) {
        viewModelScope.launch {
            getSessionFlowUseCase(sessionId).collectLatest { session ->
                if (session != null) {
                    if (_sessionName.value != session.name) {
                        _sessionName.value = session.name
                    }

                    if (session.status == SessionStatus.COMPLETED || session.status == SessionStatus.ABANDONED) {
                        _sessionEndEvent.value = SessionEndEvent.CloseScreen
                    }
                }
            }
        }
    }

    fun onStopClicked() {
        service?.stopAndAbandonSession()
    }

    private fun observeFocusViolations() {
        viewModelScope.launch {
            monitorFocusUseCase.focusViolations.collect { violation ->
                when (violation) {
                    FocusViolation.PHONE_PLACED_FACE_DOWN -> {
                        if (timerState.value?.isPlaying == false) {
                            lastStartTime = System.currentTimeMillis()
                            service?.startTimer()
                        }
                    }
                    FocusViolation.PHONE_MOVED, FocusViolation.PHONE_FLIPPED_UP -> {
                        val timeSinceStart = System.currentTimeMillis() - lastStartTime
                        if (timerState.value?.isPlaying == true && timeSinceStart > 2000) {
                            service?.pauseTimer(isViolation = true)
                            _oneTimeEvent.value = OneTimeEvent.VibrateShort
                        }
                    }
                }
            }
        }
    }

    fun onEventHandled() {
        _oneTimeEvent.value = null
    }

    fun onNavigationHandled() {
        _sessionEndEvent.value = null
    }
}