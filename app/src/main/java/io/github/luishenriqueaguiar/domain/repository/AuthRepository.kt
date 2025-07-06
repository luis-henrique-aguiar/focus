package io.github.luishenriqueaguiar.domain.repository

import io.github.luishenriqueaguiar.domain.model.User

interface AuthRepository {

    suspend fun createUser(email: String, password: String): Result<User>

    suspend fun login(email: String, password: String): Result<Unit>

    fun getCurrentUser(): User?

    fun isLogged(): Boolean

    fun logout()

}