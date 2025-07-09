package io.github.luishenriqueaguiar.ui.activities.password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.usecase.UpdateUserPasswordUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val updateUserPasswordUseCase: UpdateUserPasswordUseCase
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> get() = _updateResult

    fun onSaveChangesClicked(current: String, new: String, confirm: String) {
        if (current.isBlank() || new.isBlank() || confirm.isBlank()) {
            _updateResult.value = Result.failure(Exception("Todos os campos são obrigatórios."))
            return
        }
        if (new != confirm) {
            _updateResult.value = Result.failure(Exception("A nova senha e a confirmação não são iguais."))
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val result = updateUserPasswordUseCase(current, new)
            _updateResult.value = result
            _isLoading.value = false
        }
    }
}