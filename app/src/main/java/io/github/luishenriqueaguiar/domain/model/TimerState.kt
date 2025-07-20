package io.github.luishenriqueaguiar.domain.model

data class TimerState(
    val timeDisplay: String = "00:00",
    val progress: Int = 100,
    val isPlaying: Boolean = false
)