package com.fullwar.menuapp.data.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object AuthEventBus {
    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent

    fun emitSessionExpired() {
        _sessionExpiredEvent.tryEmit(Unit)
    }
}
