package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.repository.SessionRepository
import javax.inject.Inject

class UpdateSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(session: Session): Result<Session> {
        return sessionRepository.update(session.id, session)
    }
}