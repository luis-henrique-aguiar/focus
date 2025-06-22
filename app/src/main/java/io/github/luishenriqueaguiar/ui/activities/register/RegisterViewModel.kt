package io.github.luishenriqueaguiar.ui.activities.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _email = MutableLiveData("")
    val email: LiveData<String> get() = _email

    private val _emailError = MutableLiveData<String?>(null)
    val emailError: LiveData<String?> get() = _emailError

    private val _password = MutableLiveData("")
    val password: LiveData<String> get() = _password

    private val _passwordError = MutableLiveData<String?>(null)
    val passwordError: LiveData<String?> get() = _passwordError

    private val _name = MutableLiveData("")
    val name: LiveData<String> get() = _name

    private val _nameError = MutableLiveData<String?>(null)
    val nameError: LiveData<String?> get() = _nameError

    private val _confirmPassword = MutableLiveData("")
    val confirmPassword: LiveData<String> get() = _confirmPassword

    private val _confirmPasswordError = MutableLiveData<String?>(null)
    val confirmPasswordError: LiveData<String?> get() = _confirmPasswordError

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _registerSuccess = MutableLiveData(false)
    val registerSuccess: LiveData<Boolean> get() = _registerSuccess

    private val _generalError = MutableLiveData<String?>(null)
    val generalError: LiveData<String?> get() = _generalError

    fun updateEmail(email: String) {
        _email.value = email
    }

    fun updateName(name: String) {
        _name.value = name
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }

    fun onRegisterButtonClicked() {
        viewModelScope.launch {
            if (validateFields()) {
                _isLoading.value = true
                val result = registerUserUseCase(
                    email = _email.value!!,
                    password = _password.value!!,
                    name = _name.value!!
                )
                result.onSuccess {
                    _isLoading.value = false
                    _registerSuccess.value = true
                }.onFailure { error ->
                    _isLoading.value = false
                    _generalError.value = error.message
                }
            }
        }
    }

    fun clearInputErrors() {
        _emailError.value = null
        _nameError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
    }

    private fun validateFields(): Boolean {
        val emailResult = validateEmail(_email.value)
        val nameResult = validateName(_name.value)
        val passwordResult = validatePassword(_password.value)
        val confirmPasswordResult = validateConfirmPassword(_password.value, _confirmPassword.value)

        _emailError.value = emailResult
        _nameError.value = nameResult
        _passwordError.value = passwordResult
        _confirmPasswordError.value = confirmPasswordResult

        return emailResult == null && nameResult == null && passwordResult == null && confirmPasswordResult == null
    }

    private fun validateEmail(email: String?): String? {
        return when {
            email == null || email.isBlank() -> "Preencha o campo de e-mail"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "E-mail inválido"
            else -> null
        }
    }

    private fun validateName(name: String?): String? {
        return when {
            name == null || name.isBlank() -> "Preencha o campo de nome"
            !Regex("^[\\p{L}\\s-]+$").matches(name) -> "O nome deve conter apenas letras, espaços ou hífens"
            else -> null
        }
    }

    private fun validatePassword(password: String?): String? {
        return when {
            password == null || password.isBlank() -> "Preencha a senha"
            password.length < 6 -> "A senha deve ter pelo menos 6 caracteres"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String?, confirmPassword: String?): String? {
        return when {
            confirmPassword == null || confirmPassword.isBlank() -> "Preencha o campo de confirmação de senha"
            confirmPassword != password -> "As senhas não são iguais"
            else -> null
        }
    }
}