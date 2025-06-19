package io.github.luishenriqueaguiar.data.repository

import com.google.firebase.auth.FirebaseAuth
import io.github.luishenriqueaguiar.domain.model.User
import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

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

    override fun isLogged(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}