package io.github.luishenriqueaguiar.domain.repository

import io.github.luishenriqueaguiar.domain.model.User

interface UserRepository {

    suspend fun createUserProfile(user: User): Result<Unit>

    suspend fun updateUserName(newName: String): Result<Unit>

    suspend fun updateUserPhoto(newPhotoUrl: String): Result<Unit>

}