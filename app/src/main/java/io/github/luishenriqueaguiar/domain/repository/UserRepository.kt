package io.github.luishenriqueaguiar.domain.repository

import io.github.luishenriqueaguiar.domain.model.User

interface UserRepository {

    suspend fun create(user: User): Result<User>

    suspend fun updateUserName(newName: String): Result<Unit>

    suspend fun updateUserPhoto(newPhotoUrl: String): Result<Unit>

}