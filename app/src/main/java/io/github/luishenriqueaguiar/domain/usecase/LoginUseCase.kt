package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        if (authRepository.isLogged()) return Result.success(Unit)
        val authResult = authRepository.login(email, password)
        return if (authResult.isSuccess) Result.success(Unit) else Result.failure(authResult.exceptionOrNull()!!)
    }
}