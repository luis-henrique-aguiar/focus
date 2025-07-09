package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.model.User
import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}