package io.github.luishenriqueaguiar.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import io.github.luishenriqueaguiar.domain.repository.StorageRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor() : StorageRepository {

    private val storage = FirebaseStorage.getInstance()

    override suspend fun uploadProfileImage(imageUri: Uri, userId: String): Result<String> {
        return try {
            val storageRef = storage.reference.child("profile_images/$userId")
            storageRef.putFile(imageUri).await()

            val downloadUrl = storageRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}