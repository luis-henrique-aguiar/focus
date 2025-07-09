package io.github.luishenriqueaguiar.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.luishenriqueaguiar.domain.model.User
import io.github.luishenriqueaguiar.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun create(user: User): Result<User> {
        return try {
            firestore.collection("users")
                .document(user.uid)
                .set(user)
                .await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserName(newName: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid!!
            firestore.collection("users").document(userId)
                .update("name", newName)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserPhoto(newPhotoUrl: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid!!
            firestore.collection("users").document(userId)
                .update("profilePhoto", newPhotoUrl)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}