package io.github.luishenriqueaguiar.ui.activities.summary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.luishenriqueaguiar.domain.usecase.GetSessionByIdUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionSummaryViewModel @Inject constructor(
    private val getSessionByIdUseCase: GetSessionByIdUseCase
) : ViewModel() {

    val sessionName = MutableLiveData<String>()
    val focusTimeText = MutableLiveData<String>()
    val interruptionsText = MutableLiveData<String>()
    val insightMessage = MutableLiveData<String>()
    val pauseTimeText = MutableLiveData<String>()

    fun loadSummary(sessionId: String) {
        viewModelScope.launch {
            getSessionByIdUseCase(sessionId).onSuccess { session ->
                sessionName.value = session.name

                val focusMinutes = session.actualStudyDurationInSeconds / 60
                val focusSeconds = session.actualStudyDurationInSeconds % 60
                focusTimeText.value = "Tempo Focado: ${focusMinutes}m ${focusSeconds}s"

                interruptionsText.value = "Interrupções: ${session.interruptionsCount} vezes"

                val pauseMinutes = session.totalPauseDurationInSeconds / 60
                val pauseSeconds = session.totalPauseDurationInSeconds % 60
                pauseTimeText.value = "Tempo em Pausa: ${pauseMinutes}m ${pauseSeconds}s"

                insightMessage.value = if (session.interruptionsCount == 0) {
                    "Foco perfeito! Você completou a sessão sem nenhuma interrupção. Continue assim!"
                } else {
                    "Ótimo esforço! Na próxima, tente reduzir o número de interrupções para um foco ainda mais profundo."
                }
            }
        }
    }
}