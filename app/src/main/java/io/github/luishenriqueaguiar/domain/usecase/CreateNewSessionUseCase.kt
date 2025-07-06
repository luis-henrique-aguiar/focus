package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.model.SessionStatus
import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import io.github.luishenriqueaguiar.domain.repository.SessionRepository
import java.util.UUID
import javax.inject.Inject

class CreateNewSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        name: String,
        plannedStudyDurationInMinutes: Int,
        plannedBreakDurationInMinutes: Int
    ): Result<Session> {

        val currentUser = authRepository.getCurrentUser()
            ?: return Result.failure(Exception("Nenhum usuário logado para criar a sessão."))

        val newSession = Session(
            id = UUID.randomUUID().toString(),
            userId = currentUser.uid,
            name = name,
            plannedStudyDurationInMinutes = plannedStudyDurationInMinutes,
            plannedBreakDurationInMinutes = plannedBreakDurationInMinutes,
            status = SessionStatus.PLANNED,
            startTime = null,
            endTime = null,
            actualStudyDurationInSeconds = 0,
            totalBreakDurationInSeconds = 0
        )

        return sessionRepository.save(newSession)
    }
}