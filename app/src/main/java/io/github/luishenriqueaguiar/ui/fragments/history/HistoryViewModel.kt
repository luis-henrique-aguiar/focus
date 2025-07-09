package io.github.luishenriqueaguiar.ui.fragments.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.usecase.GetSessionHistoryUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getSessionHistoryUseCase: GetSessionHistoryUseCase
) : ViewModel() {

    private val _sessions = MutableLiveData<List<Session>>()
    val sessions: LiveData<List<Session>> get() = _sessions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadHistory()
    }

    private fun loadHistory() {
        _isLoading.value = true
        viewModelScope.launch {
            getSessionHistoryUseCase()?.onSuccess { sessionList ->
                _sessions.value = sessionList
            }?.onFailure {
            }
            _isLoading.value = false
        }
    }
}