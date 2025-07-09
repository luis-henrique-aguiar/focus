package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.model.HistoryFilter
import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import io.github.luishenriqueaguiar.domain.repository.SessionRepository
import java.util.Calendar
import javax.inject.Inject

class GetSessionHistoryUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(filter: HistoryFilter) = authRepository.getCurrentUser()?.uid?.let { userId ->
        val calendar = Calendar.getInstance()
        when (filter) {
            HistoryFilter.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
            }
            HistoryFilter.WEEK -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            HistoryFilter.MONTH -> calendar.add(Calendar.MONTH, -1)
        }
        val fromDate = calendar.time

        sessionRepository.getSessionHistory(userId, fromDate)
    }
}