package io.github.luishenriqueaguiar.ui.activities.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _emailError = MutableLiveData<String?>(null)
    val emailError: LiveData<String?> get() = _emailError

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _passwordError = MutableLiveData<String?>(null)
    val passwordError: LiveData<String?> get() = _passwordError

    private val _generalError = MutableLiveData<String?>(null)
    val generalError: LiveData<String?> get() = _generalError

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _loginSuccess = MutableLiveData(false)
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateEmail(email: String) {
        _email.value = email
    }

    fun onLoginButtonClicked() {
        if (validateFields()) {
            viewModelScope.launch {
                _loading.value = true
                val authResult = loginUseCase(email = _email.value!!, password = _password.value!!)
                authResult.onSuccess {
                    _loading.value = false
                    _loginSuccess.value = true
                }
                authResult.onFailure { error ->
                    _loading.value = false
                    _generalError.value = when (error) {
                        is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha inválidos."
                        is FirebaseAuthInvalidUserException -> "Usuário não encontrado."
                        else -> "Ocorreu um erro. Tente novamente."
                    }
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        val emailResult = validateEmail(_email.value)
        _emailError.value = emailResult

        val passwordResult = validatePassword(_password.value)
        _passwordError.value = passwordResult

        return emailResult == null && passwordResult == null
    }

    private fun validateEmail(email: String?): String? {
        return when {
            email.isNullOrBlank() -> "Preencha o campo de e-mail"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "E-mail inválido"
            else -> null
        }
    }

    private fun validatePassword(password: String?): String? {
        return when {
            password.isNullOrBlank() -> "Preencha a senha"
            else -> null
        }
    }
}