package dev.prince.taskify.util

import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

fun <T> oneShotFlow() = MutableSharedFlow<T>(
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

val LocalSnackbar = compositionLocalOf<(String) -> Unit> { { } }
