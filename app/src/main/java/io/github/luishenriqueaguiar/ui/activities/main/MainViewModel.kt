package io.github.luishenriqueaguiar.ui.activities.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.model.SessionStatus
import io.github.luishenriqueaguiar.domain.usecase.GetInProgressSessionUseCase
import io.github.luishenriqueaguiar.domain.usecase.UpdateSessionUseCase
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getInProgressSessionUseCase: GetInProgressSessionUseCase,
    private val updateSessionUseCase: UpdateSessionUseCase
) : ViewModel() {

    private val _inProgressSession = MutableLiveData<Session?>()
    val inProgressSession: LiveData<Session?> get() = _inProgressSession

    init {
        checkForInProgressSession()
    }

    private fun checkForInProgressSession() {
        viewModelScope.launch {
            getInProgressSessionUseCase().onSuccess { session ->
                _inProgressSession.value = session
            }
        }
    }

    fun onAbandonSessionClicked(session: Session) {
        viewModelScope.launch {
            val updatedSession = session.copy(status = SessionStatus.ABANDONED, endTime = Date())
            updateSessionUseCase(updatedSession)
            _inProgressSession.value = null
        }
    }
}