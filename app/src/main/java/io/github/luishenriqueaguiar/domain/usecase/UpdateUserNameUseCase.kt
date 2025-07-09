package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import io.github.luishenriqueaguiar.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(newName: String): Result<Unit> {
        if (newName.isBlank()) {
            return Result.failure(IllegalArgumentException("O nome não pode ser vazio."))
        }
        authRepository.updateUserName(newName).onFailure { error ->
            return Result.failure(error)
        }
        return userRepository.updateUserName(newName)
    }
}