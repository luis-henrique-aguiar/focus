package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionFlowUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(sessionId: String): Flow<Session?> {
        return sessionRepository.getSessionFlow(sessionId)
    }
}