package io.github.luishenriqueaguiar.ui.activities.focus

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.usecase.GetSessionByIdUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FocusSessionViewModel @Inject constructor(
    private val getSessionByIdUseCase: GetSessionByIdUseCase
) : ViewModel() {

    private val _session = MutableLiveData<Session>()
    val session: LiveData<Session> get() = _session

    val timeDisplay: LiveData<String> = _session.map {
        val minutes = it.plannedStudyDurationInMinutes
        String.format("%02d:00", minutes)
    }

    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            val result = getSessionByIdUseCase(sessionId)
            result.onSuccess { sessionData ->
                _session.value = sessionData
            }.onFailure {
            }
        }
    }
}