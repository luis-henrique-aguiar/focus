package io.github.luishenriqueaguiar.domain.model

import java.util.Date

data class Session (
    val id: String,
    val userId: String,
    val name: String,
    val plannedStudyDurationInMinutes: Int,
    val plannedBreakDurationInMinutes: Int,
    val status: SessionStatus,
    val startTime: Date?,
    val endTime: Date?,
    val actualStudyDurationInSeconds: Long,
    val totalBreakDurationInSeconds: Long
)