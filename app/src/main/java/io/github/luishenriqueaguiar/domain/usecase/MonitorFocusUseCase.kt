package io.github.luishenriqueaguiar.domain.usecase

import io.github.luishenriqueaguiar.domain.repository.SensorRepository
import javax.inject.Inject

class MonitorFocusUseCase @Inject constructor(
    private val sensorRepository: SensorRepository
) {
    val focusViolations = sensorRepository.focusViolations
    fun start() = sensorRepository.startMonitoring()
    fun stop() = sensorRepository.stopMonitoring()
}