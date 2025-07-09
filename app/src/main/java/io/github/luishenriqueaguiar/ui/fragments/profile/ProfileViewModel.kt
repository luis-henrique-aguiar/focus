package io.github.luishenriqueaguiar.ui.fragments.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.model.User
import io.github.luishenriqueaguiar.domain.usecase.GetCurrentUserUseCase
import io.github.luishenriqueaguiar.domain.usecase.LogoutUseCase
import io.github.luishenriqueaguiar.domain.usecase.UpdateUserNameUseCase
import io.github.luishenriqueaguiar.domain.usecase.UpdateUserPhotoUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val updateUserPhotoUseCase: UpdateUserPhotoUseCase
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _logoutComplete = MutableLiveData<Unit?>()
    val logoutComplete: LiveData<Unit?> get() = _logoutComplete

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _updateMessage = MutableLiveData<String?>()
    val updateMessage: LiveData<String?> get() = _updateMessage

    init {
        loadUserData()
    }

    private fun loadUserData() {
        _user.value = getCurrentUserUseCase()
    }

    fun onLogoutClicked() {
        logoutUseCase()
        _logoutComplete.value = Unit
    }

    fun onProfileImageSelected(imageUri: Uri) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = updateUserPhotoUseCase(imageUri)
            result.onSuccess {
                _updateMessage.value = "Foto de perfil atualizada!"
                loadUserData()
            }.onFailure {
                _updateMessage.value = it.message ?: "Erro ao atualizar a foto."
            }
            _isLoading.value = false
        }
    }

    fun onUpdateName(newName: String) {
        _isLoading.value = true
        viewModelScope.launch  {
            val result = updateUserNameUseCase(newName)
            result.onSuccess {
                _updateMessage.value = "Nome atualizado com sucesso!"
                loadUserData()
            }.onFailure {
                _updateMessage.value = it.message ?: "Erro ao atualizar o nome."
            }
            _isLoading.value = false
        }
    }

    fun onUpdateMessageShown() {
        _updateMessage.value = null
    }
}