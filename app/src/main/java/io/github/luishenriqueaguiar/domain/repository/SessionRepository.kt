package io.github.luishenriqueaguiar.domain.repository

import io.github.luishenriqueaguiar.domain.model.Session

interface SessionRepository {

    suspend fun save(session: Session): Result<Session>

    suspend fun getById(id: String): Result<Session>

    suspend fun update(id: String, session: Session): Result<Session>

    suspend fun getInProgressSession(): Result<Session?>

}