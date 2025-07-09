package io.github.luishenriqueaguiar.domain.usecase

import android.net.Uri
import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import io.github.luishenriqueaguiar.domain.repository.StorageRepository
import io.github.luishenriqueaguiar.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserPhotoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(imageUri: Uri): Result<Unit> {
        val userId = authRepository.getCurrentUser()?.uid
            ?: return Result.failure(Exception("Usuário não encontrado para fazer o upload."))

        val uploadResult = storageRepository.uploadProfileImage(imageUri, userId)
        val photoUrl = uploadResult.getOrNull()
            ?: return Result.failure(uploadResult.exceptionOrNull()!!)

        authRepository.updateUserPhoto(photoUrl).onFailure { error ->
            return Result.failure(error)
        }

        return userRepository.updateUserPhoto(photoUrl)
    }
}