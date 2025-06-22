package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.model.User
import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import io.github.luishenriqueaguiar.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<User> {
        val authResult = authRepository.createUser(email, password)
        val createdAuthUser = authResult.getOrNull()

        if (createdAuthUser == null) {
            return Result.failure(authResult.exceptionOrNull() ?: Exception("Erro desconhecido na autenticação"))
        }

        val userWithDetails = User(uid = createdAuthUser.uid, email = email, name = name, profilePhoto = null)
        val databaseResult = userRepository.create(userWithDetails)

        return databaseResult.map {
            userWithDetails
        }
    }
}