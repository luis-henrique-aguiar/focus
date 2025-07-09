package io.github.luishenriqueaguiar.ui.fragments.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.usecase.GetCurrentUserUseCase
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _greetingText = MutableLiveData<String>()
    val greetingText: LiveData<String> get() = _greetingText

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val user = getCurrentUserUseCase()
        _greetingText.value = "Bom dia, ${user?.name ?: "Usuário"}!"
    }
}