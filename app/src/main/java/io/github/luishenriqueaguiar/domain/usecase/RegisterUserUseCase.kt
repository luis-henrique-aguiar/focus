package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.model.User
import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (password.length < 8) {
            return Result.failure(Exception("A senha deve ter no mínimo 8 caracteres."))
        }

        if (!email.contains("@")) {
            return Result.failure(Exception("Formato de e-mail inválido."))
        }

        return authRepository.createUser(email, password)
    }
}