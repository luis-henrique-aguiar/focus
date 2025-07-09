package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateUserPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(currentPassword: String, newPassword: String): Result<Unit> {
        if (newPassword.length < 6) {
            return Result.failure(IllegalArgumentException("A nova senha deve ter no mínimo 6 caracteres."))
        }

        val reauthResult = authRepository.reauthenticate(currentPassword)
        if (reauthResult.isFailure) {
            return Result.failure(Exception("A senha atual está incorreta."))
        }

        return authRepository.updateUserPassword(newPassword)
    }
}