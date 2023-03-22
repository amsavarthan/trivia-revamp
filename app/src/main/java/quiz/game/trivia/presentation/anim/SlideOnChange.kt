package quiz.game.trivia.presentation.anim

import androidx.compose.animation.*
import androidx.compose.runtime.Composable

sealed class SlideDirection {
    object Up : SlideDirection()
    object Down : SlideDirection()
    object Adaptive : SlideDirection()
}

@OptIn(ExperimentalAnimationApi::class)
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

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentScope<Int>.getTransitionSpec(
    direction: SlideDirection,
    clipToContainer: Boolean
) = when (direction) {
    SlideDirection.Up -> {
        fadeIn() + slideInVertically(initialOffsetY = { height -> height }) with
                fadeOut() + slideOutVertically(targetOffsetY = { height -> -height })
    }
    SlideDirection.Down -> {
        fadeIn() + slideInVertically(initialOffsetY = { height -> -height }) with
                fadeOut() + slideOutVertically(targetOffsetY = { height -> height })
    }
    SlideDirection.Adaptive -> {
        if (targetState > initialState) {
            fadeIn() + slideInHorizontally(initialOffsetX = { height -> height }) with
                    fadeOut() + slideOutHorizontally(targetOffsetX = { height -> -height })
        } else {
            fadeIn() + slideInHorizontally(initialOffsetX = { height -> -height }) with
                    fadeOut() + slideOutHorizontally(targetOffsetX = { height -> height })
        }
    }
} using SizeTransform(clip = clipToContainer)
