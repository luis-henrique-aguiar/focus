package io.github.luishenriqueaguiar.domain.repository

import io.github.luishenriqueaguiar.domain.model.FocusViolation
import kotlinx.coroutines.flow.Flow

interface SensorRepository {

    val focusViolations: Flow<FocusViolation>

    fun startMonitoring()

    fun stopMonitoring()

}