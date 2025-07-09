package io.github.luishenriqueaguiar.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import io.github.luishenriqueaguiar.domain.model.User
import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import androidx.core.net.toUri

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override suspend fun createUser(email: String, password: String): Result<User> {
        return try {
            val userCredential = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = userCredential.user!!
            val domainUser = User(uid = firebaseUser.uid, email = firebaseUser.email, name = null, profilePhoto = null)
            Result.success(domainUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser

        return firebaseUser?.let {
            User(uid = it.uid, email = it.email, name = it.displayName, profilePhoto = it.photoUrl?.toString())
        }
    }

    override fun isLogged(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun updateUserName(newName: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser!!
            val profileUpdates = userProfileChangeRequest {
                displayName = newName
            }
            user.updateProfile(profileUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserPhoto(newPhotoUrl: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser!!
            val profileUpdates = userProfileChangeRequest {
                photoUri = newPhotoUrl.toUri()
            }
            user.updateProfile(profileUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}