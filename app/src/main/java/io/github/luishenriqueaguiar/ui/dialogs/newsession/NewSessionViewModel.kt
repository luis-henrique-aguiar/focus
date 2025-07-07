package io.github.luishenriqueaguiar.ui.dialogs.newsession

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.usecase.CreateNewSessionUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewSessionViewModel @Inject constructor(
    private val createNewSessionUseCase: CreateNewSessionUseCase
) : ViewModel() {

    private val _sessionName = MutableLiveData("")

    private val _sessionNameError = MutableLiveData<String?>(null)
    val sessionNameError: LiveData<String?> get() = _sessionNameError

    private val _focusDurationInMinutes = MutableLiveData(25)
    val focusDurationText: LiveData<String> = _focusDurationInMinutes.map { "Tempo de Foco: $it min" }

    private val _breakDurationInMinutes = MutableLiveData(5)
    val breakDurationText: LiveData<String> = _breakDurationInMinutes.map { "Tempo de Pausa: $it min" }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _navigateToFocusSession = MutableLiveData<Session?>()
    val navigateToFocusSession: LiveData<Session?> get() = _navigateToFocusSession

    private val _generalError = MutableLiveData<String?>(null)
    val generalError: LiveData<String?> get() = _generalError

    fun onSessionNameChanged(name: String) {
        _sessionName.value = name
        _sessionNameError.value = validateSessionName()
    }

    fun onFocusDurationChanged(duration: Float) {
        _focusDurationInMinutes.value = duration.toInt()
    }

    fun onBreakDurationChanged(duration: Float) {
        _breakDurationInMinutes.value = duration.toInt()
    }

    fun onNavigationHandled() {
        _navigateToFocusSession.value = null
    }

    fun onStartSessionClicked() {
        viewModelScope.launch {
            if (validateFields()) {
                _isLoading.value = true
                val result = createNewSessionUseCase(
                    name = _sessionName.value!!,
                    plannedBreakDurationInMinutes = _breakDurationInMinutes.value!!,
                    plannedStudyDurationInMinutes = _focusDurationInMinutes.value!!
                )
                result.onSuccess { newSession ->
                    _isLoading.value = false
                    _navigateToFocusSession.value = newSession
                }
                result.onFailure { error ->
                    _isLoading.value = false
                    _generalError.value = error.message
                }
            }
        }
    }

    private fun validateSessionName(): String? {
        return when {
            _sessionName.value.isNullOrBlank() -> "O nome da sessão não pode estar vazio."
            else -> null
        }
    }

    private fun validateFields(): Boolean {
        _sessionNameError.value = validateSessionName()
        return _sessionNameError.value == null &&
            _breakDurationInMinutes.value != null &&
            _focusDurationInMinutes.value != null
    }
}