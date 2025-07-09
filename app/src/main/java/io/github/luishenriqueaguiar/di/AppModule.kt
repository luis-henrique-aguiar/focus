package io.github.luishenriqueaguiar.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.luishenriqueaguiar.data.repository.AuthRepositoryImpl
import io.github.luishenriqueaguiar.data.repository.SensorRepositoryImpl
import io.github.luishenriqueaguiar.data.repository.SessionRepositoryImpl
import io.github.luishenriqueaguiar.data.repository.StorageRepositoryImpl
import io.github.luishenriqueaguiar.data.repository.UserRepositoryImpl
import io.github.luishenriqueaguiar.domain.repository.AuthRepository
import io.github.luishenriqueaguiar.domain.repository.SensorRepository
import io.github.luishenriqueaguiar.domain.repository.SessionRepository
import io.github.luishenriqueaguiar.domain.repository.StorageRepository
import io.github.luishenriqueaguiar.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindStorageRepository(
        impl: StorageRepositoryImpl
    ): StorageRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindSensorRepository(
        sensorRepositoryImpl: SensorRepositoryImpl
    ): SensorRepository
}