package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import javax.inject.Inject

class CheckUserIsLoggedUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.isLogged()
    }
}