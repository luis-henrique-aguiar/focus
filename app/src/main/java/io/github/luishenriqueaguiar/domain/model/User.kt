package io.github.luishenriqueaguiar.domain.model

data class User(
    val uid: String,
    val name: String?,
    val email: String?,
    val profilePhoto: String?
)