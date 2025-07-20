package io.github.luishenriqueaguiar.ui.utils

sealed class SessionEndEvent {
    object CloseScreen : SessionEndEvent()
}