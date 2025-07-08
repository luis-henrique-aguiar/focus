package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.repository.SessionRepository
import javax.inject.Inject

class GetInProgressSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Result<Session?> {
        return sessionRepository.getInProgressSession()
    }
}