package com.app.tasty.utilities

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    fun Observe(): Flow<Status>

    enum class Status {
        Unavailable, Lost, Available, Losing
    }
}