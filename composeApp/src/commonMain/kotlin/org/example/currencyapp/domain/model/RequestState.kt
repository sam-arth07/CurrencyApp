package org.example.currencyapp.domain.model

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()

    data class Success<out T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()

    /* *
    * Utility Functions to return boolean values based on the current state
    * Can be useful to update UI according to request State.
    * */
    fun isLoading(): Boolean = this is Loading
    fun isError(): Boolean = this is Error
    fun isSuccess(): Boolean = this is Success

    /* *
    * Function to extract generic data from success state and error message from an error state
    * */
    fun getSuccessData() = (this as Success).data
    fun getErrorMessage(): String = (this as Error).message
}

@Composable
fun <T> RequestState<T>.DisplayResult(
    onIdle: (@Composable () -> Unit)? = null,
    onError: (@Composable (String) -> Unit)? = null,
    onLoading: (@Composable () -> Unit)? = null,
    onSuccess: (@Composable (T) -> Unit),
    transitionSpec: ContentTransform = scaleIn(tween(durationMillis = 300))
            + fadeIn(tween(durationMillis = 600))
            togetherWith scaleOut(tween(durationMillis = 300))
            + fadeOut(tween(durationMillis = 600))
) {
    AnimatedContent(
        transitionSpec = { transitionSpec },
        label = "Content Animation",
        targetState = this
    ) { state ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            when (state) {
                is RequestState.Error -> {
                    onError?.invoke(state.getErrorMessage())
                }

                RequestState.Idle -> {
                    onIdle?.invoke()
                }

                RequestState.Loading -> {
                    onLoading?.invoke()
                }

                is RequestState.Success<*> -> {
                    onSuccess(state.getSuccessData())
                }
            }
        }
    }
}