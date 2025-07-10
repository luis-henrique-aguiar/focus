package io.github.luishenriqueaguiar.ui.utils

sealed class OneTimeEvent {

    object VibrateShort : OneTimeEvent()

    object VibrateLong : OneTimeEvent()

}