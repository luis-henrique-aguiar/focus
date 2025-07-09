package io.github.luishenriqueaguiar.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.model.SessionStatus
import io.github.luishenriqueaguiar.domain.repository.SessionRepository
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor() : SessionRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun save(session: Session): Result<Session> {
        return try {
            firestore
                .collection("sessions")
                .document(session.id)
                .set(session)
                .await()
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getById(id: String): Result<Session> {
        return try {
            val documentSnapshot = firestore.collection("sessions")
                .document(id)
                .get()
                .await()

            val session = documentSnapshot.toObject(Session::class.java)

            if (session != null) {
                Result.success(session)
            } else {
                Result.failure(Exception("Sessão não encontrada com o ID: $id"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun update(id: String, session: Session): Result<Session> {
        return try {
            firestore.collection("sessions")
                .document(id)
                .set(session)
                .await()
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInProgressSession(): Result<Session?> {
        val userId = auth.currentUser?.uid ?: return Result.success(null)
        return try {
            val querySnapshot = firestore.collection("sessions")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", SessionStatus.IN_PROGRESS.name)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                Result.success(null)
            } else {
                val session = querySnapshot.documents.first().toObject(Session::class.java)
                Result.success(session)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSessionsForUser(userId: String, fromDate: Date, toDate: Date): Result<List<Session>> {
        return try {
            val querySnapshot = firestore.collection("sessions")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("startTime", fromDate)
                .whereLessThanOrEqualTo("startTime", toDate)
                .get()
                .await()

            val sessions = querySnapshot.toObjects(Session::class.java)
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSessionHistory(userId: String, fromDate: Date): Result<List<Session>> {
        return try {
            val querySnapshot = firestore.collection("sessions")
                .whereEqualTo("userId", userId)
                .whereIn("status", listOf(SessionStatus.COMPLETED.name, SessionStatus.ABANDONED.name))
                .whereGreaterThanOrEqualTo("startTime", fromDate)
                .orderBy("startTime", Query.Direction.DESCENDING)
                .get()
                .await()

            val sessions = querySnapshot.toObjects(Session::class.java)
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}