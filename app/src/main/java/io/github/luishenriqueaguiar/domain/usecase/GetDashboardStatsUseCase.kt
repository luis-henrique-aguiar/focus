package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.model.DashboardStats
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.model.SessionStatus
import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import io.github.luishenriqueaguiar.domain.repository.SessionRepository
import java.util.Calendar
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<DashboardStats> {
        val userId = authRepository.getCurrentUser()?.uid ?: return Result.success(DashboardStats())

        val calendar = Calendar.getInstance()
        val toDate = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val fromDate = calendar.time

        val sessionsResult = sessionRepository.getSessionsForUser(userId, fromDate, toDate)

        return sessionsResult.map { sessions ->
            val completedSessions = sessions.filter { it.status == SessionStatus.COMPLETED }
            calculateStats(completedSessions)
        }
    }

    private fun calculateStats(sessions: List<Session>): DashboardStats {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        val todayTime = sessions
            .filter { it.startTime != null && it.startTime.after(todayStart) }
            .sumOf { it.actualStudyDurationInSeconds }

        val weekTime = sessions.sumOf { it.actualStudyDurationInSeconds }

        val distinctDays = sessions.mapNotNull { it.startTime }.map {
            val cal = Calendar.getInstance()
            cal.time = it
            cal.get(Calendar.DAY_OF_YEAR)
        }.toSet().size

        return DashboardStats(
            totalFocusTimeTodayInSeconds = todayTime,
            totalFocusTimeWeekInSeconds = weekTime,
            currentStreakInDays = distinctDays
        )
    }
}