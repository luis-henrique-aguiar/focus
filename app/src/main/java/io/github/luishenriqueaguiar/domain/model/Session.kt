package io.github.luishenriqueaguiar.domain.model

import java.util.Date

data class Session(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val plannedStudyDurationInMinutes: Int = 0,
    val plannedBreakDurationInMinutes: Int = 0,
    val status: SessionStatus = SessionStatus.PLANNED,
    val startTime: Date? = null,
    val endTime: Date? = null,
    val actualStudyDurationInSeconds: Long = 0L,
    val totalBreakDurationInSeconds: Long = 0L,
    val interruptionsCount: Int = 0,
    val totalPauseDurationInSeconds: Long = 0L
)