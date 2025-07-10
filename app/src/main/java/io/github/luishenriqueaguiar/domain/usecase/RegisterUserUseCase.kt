package io.github.luishenriqueaguiar.domain.usecase

import android.net.Uri
import io.github.luishenriqueaguiar.domain.model.User
import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import io.github.luishenriqueaguiar.domain.repository.StorageRepository
import io.github.luishenriqueaguiar.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String, profileImageUri: Uri?): Result<Unit> {
        val authResult = authRepository.createUser(email, password)
        val createdAuthUser = authResult.getOrNull()
            ?: return Result.failure(authResult.exceptionOrNull()!!)

        var profilePhotoUrl: String? = null
        if (profileImageUri != null) {
            val uploadResult = storageRepository.uploadProfileImage(profileImageUri, createdAuthUser.uid)
            profilePhotoUrl = uploadResult.getOrNull()
            if (profilePhotoUrl == null) {
                return Result.failure(uploadResult.exceptionOrNull()!!)
            }
        }

        val updateProfileResult = authRepository.updateUserNameAndPhoto(name, profilePhotoUrl)
        if (updateProfileResult.isFailure) {
            return updateProfileResult
        }

        val userWithDetails = User(
            uid = createdAuthUser.uid,
            email = email,
            name = name,
            profilePhoto = profilePhotoUrl
        )
        return userRepository.createUserProfile(userWithDetails)
    }
}