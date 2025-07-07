package io.github.luishenriqueaguiar.domain.usecase

import android.util.Log
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.repository.SessionRepository
import javax.inject.Inject

class GetSessionByIdUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(sessionId: String): Result<Session> {
        return sessionRepository.getById(sessionId)
    }
}