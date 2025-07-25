package io.github.luishenriqueaguiar.domain.repository

import io.github.luishenriqueaguiar.domain.model.Session
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface SessionRepository {

    suspend fun save(session: Session): Result<Session>

    suspend fun getById(id: String): Result<Session>

    suspend fun update(id: String, session: Session): Result<Session>

    suspend fun getInProgressSession(): Result<Session?>

    suspend fun getSessionsForUser(userId: String, fromDate: Date, toDate: Date): Result<List<Session>>

    suspend fun getSessionHistory(userId: String, fromDate: Date): Result<List<Session>>

    fun getSessionFlow(sessionId: String): Flow<Session?>

}