package io.github.luishenriqueaguiar.domain.model

data class DashboardStats(
    val totalFocusTimeTodayInSeconds: Long = 0,
    val totalFocusTimeWeekInSeconds: Long = 0,
    val currentStreakInDays: Int = 0
)