package io.github.luishenriqueaguiar.ui.fragments.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.model.User
import io.github.luishenriqueaguiar.domain.usecase.GetCurrentUserUseCase
import io.github.luishenriqueaguiar.domain.usecase.GetDashboardStatsUseCase
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _greetingText = MutableLiveData<String>()
    val greetingText: LiveData<String> get() = _greetingText

    private val _todayStatsText = MutableLiveData("Tempo focado hoje: 0m")
    val todayStatsText: LiveData<String> get() = _todayStatsText

    private val _weeklyAverageText = MutableLiveData("Média diária: 0m")
    val weeklyAverageText: LiveData<String> get() = _weeklyAverageText

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val currentUser = getCurrentUserUseCase()
        _user.value = currentUser
        _greetingText.value = getGreetingMessage(currentUser?.name)

        viewModelScope.launch {
            getDashboardStatsUseCase().onSuccess { stats ->
                val todayMinutes = stats.totalFocusTimeTodayInSeconds / 60
                _todayStatsText.value = "Tempo focado hoje: ${todayMinutes}m"
            }
        }
    }

    private fun getGreetingMessage(name: String?): String {
        val calendar = Calendar.getInstance()
        val greeting = when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Bom dia"
            in 12..17 -> "Boa tarde"
            else -> "Boa noite"
        }
        return "$greeting, ${name ?: "Usuário"}!"
    }
}