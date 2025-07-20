package io.github.luishenriqueaguiar.data.repository

import io.github.luishenriqueaguiar.domain.model.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerStateRepository @Inject constructor() {

    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    fun updateState(newState: TimerState) {
        _timerState.value = newState
    }

    fun resetState() {
        _timerState.value = TimerState()
    }
}