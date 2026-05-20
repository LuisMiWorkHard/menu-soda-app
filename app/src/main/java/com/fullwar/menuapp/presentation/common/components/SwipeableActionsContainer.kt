package com.fullwar.menuapp.presentation.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.ui.theme.White
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class SwipeAction(
    val icon: ImageVector,
    val contentDescription: String,
    val backgroundColor: Color,
    val onClick: () -> Unit
)

private val ACTION_BUTTON_WIDTH = 60.dp

@Composable
fun SwipeableActionsContainer(
    isOpen: Boolean,
    onOpen: () -> Unit,
    onClose: () -> Unit,
    actions: List<SwipeAction>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scope            = rememberCoroutineScope()
    val offsetAnim       = remember { Animatable(0f) }
    val actionsTotalWidth: Dp = ACTION_BUTTON_WIDTH * actions.size

    LaunchedEffect(isOpen) {
        if (!isOpen) offsetAnim.animateTo(
            targetValue   = 0f,
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds()
    ) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .offset {
                    IntOffset(
                        x = actionsTotalWidth.toPx().roundToInt() + offsetAnim.value.roundToInt(),
                        y = 0
                    )
                },
            horizontalArrangement = Arrangement.End
        ) {
            actions.forEach { action ->
                Box(
                    modifier = Modifier
                        .width(ACTION_BUTTON_WIDTH)
                        .fillMaxHeight()
                        .background(action.backgroundColor)
                        .clickable {
                            scope.launch {
                                offsetAnim.animateTo(
                                    0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                                )
                            }
                            onClose()
                            action.onClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.contentDescription,
                        tint = White
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetAnim.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    val maxOffsetPx = -actionsTotalWidth.toPx()
                    val minSwipePx  = 4.dp.toPx()
                    var wasOpen = false

                    detectHorizontalDragGestures(
                        onDragStart = {
                            wasOpen = offsetAnim.value <= maxOffsetPx * 0.5f
                        },
                        onHorizontalDrag = { _, delta ->
                            val newOffset = (offsetAnim.value + delta).coerceIn(maxOffsetPx, 0f)
                            scope.launch { offsetAnim.snapTo(newOffset) }
                        },
                        onDragEnd = {
                            val shouldOpen = if (wasOpen) {
                                offsetAnim.value <= maxOffsetPx + minSwipePx
                            } else {
                                offsetAnim.value < -minSwipePx
                            }
                            val target = if (shouldOpen) maxOffsetPx else 0f
                            scope.launch {
                                offsetAnim.animateTo(
                                    targetValue   = target,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness    = Spring.StiffnessMedium
                                    )
                                )
                                if (shouldOpen) onOpen() else onClose()
                            }
                        },
                        onDragCancel = {
                            val target = if (wasOpen) maxOffsetPx else 0f
                            scope.launch {
                                offsetAnim.animateTo(
                                    target, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                                )
                                if (wasOpen) onOpen() else onClose()
                            }
                        }
                    )
                }
        ) {
            content()
            if (isOpen) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(Unit) {
                            detectTapGestures {
                                scope.launch {
                                    offsetAnim.animateTo(
                                        0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                                    )
                                }
                                onClose()
                            }
                        }
                )
            }
        }
    }
}
