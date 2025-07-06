package io.github.luishenriqueaguiar.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.repository.SessionRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor() : SessionRepository {

    private val firestore = FirebaseFirestore.getInstance()

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
}