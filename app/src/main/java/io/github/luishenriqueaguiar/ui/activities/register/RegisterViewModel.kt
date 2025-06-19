package io.github.luishenriqueaguiar.ui.activities.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.usecase.RegisterUserUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    fun onRegisterButtonClicked(email: String, password: String) {
        viewModelScope.launch {
            val registrationResult = registerUserUseCase(email, password)
            registrationResult.onSuccess { user ->

            }.onFailure { error ->

            }
        }
    }
}