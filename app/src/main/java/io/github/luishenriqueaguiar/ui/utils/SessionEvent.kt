package io.github.luishenriqueaguiar.ui.utils

sealed class SessionEndEvent {
    data class NavigateToSummary(val sessionId: String) : SessionEndEvent()
}