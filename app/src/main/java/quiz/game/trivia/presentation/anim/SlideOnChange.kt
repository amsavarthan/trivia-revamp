package quiz.game.trivia.presentation.anim

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable

sealed class SlideDirection {
    data object Up : SlideDirection()
    data object Down : SlideDirection()
    data object Adaptive : SlideDirection()
}

@Composable
fun SlideOnChange(
    targetState: Int,
    clipToContainer: Boolean = false,
    direction: SlideDirection,
    content: @Composable (Int) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            getTransitionSpec(
                direction = direction,
                clipToContainer = clipToContainer
            )
        }
    ) { state ->
        content(state)
    }
}

private fun AnimatedContentTransitionScope<Int>.getTransitionSpec(
    direction: SlideDirection,
    clipToContainer: Boolean
) = when (direction) {
    SlideDirection.Up -> {
        fadeIn() + slideInVertically(initialOffsetY = { height -> height }) togetherWith
                fadeOut() + slideOutVertically(targetOffsetY = { height -> -height })
    }

    SlideDirection.Down -> {
        fadeIn() + slideInVertically(initialOffsetY = { height -> -height }) togetherWith
                fadeOut() + slideOutVertically(targetOffsetY = { height -> height })
    }

    SlideDirection.Adaptive -> {
        if (targetState > initialState) {
            fadeIn() + slideInHorizontally(initialOffsetX = { height -> height }) togetherWith
                    fadeOut() + slideOutHorizontally(targetOffsetX = { height -> -height })
        } else {
            fadeIn() + slideInHorizontally(initialOffsetX = { height -> -height }) togetherWith
                    fadeOut() + slideOutHorizontally(targetOffsetX = { height -> height })
        }
    }
} using SizeTransform(clip = clipToContainer)
