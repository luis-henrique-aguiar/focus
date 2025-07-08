package io.github.luishenriqueaguiar.ui.activities.initial

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.usecase.CheckUserIsLoggedUseCase
import javax.inject.Inject

@HiltViewModel
class InitialViewModel @Inject constructor(
    private val checkUserIsLoggedUseCase: CheckUserIsLoggedUseCase
) : ViewModel() {

    private val _isUserLogged = MutableLiveData<Boolean>()
    val isUserLogged: LiveData<Boolean> get() = _isUserLogged

    fun checkUserStatus() {
        val isLoggedIn = checkUserIsLoggedUseCase()
        _isUserLogged.value = isLoggedIn
    }
}