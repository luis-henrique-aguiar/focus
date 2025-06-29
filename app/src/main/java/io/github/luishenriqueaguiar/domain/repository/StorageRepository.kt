package io.github.luishenriqueaguiar.domain.repository

import android.net.Uri

interface StorageRepository {

    suspend fun uploadProfileImage(imageUri: Uri, userId: String): Result<String>

}