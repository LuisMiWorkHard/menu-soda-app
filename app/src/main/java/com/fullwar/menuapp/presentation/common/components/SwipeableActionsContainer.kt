package com.fullwar.menuapp.presentation.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.ui.theme.White
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val ACTION_BUTTON_WIDTH = 60.dp
private val ACTION_TOTAL_WIDTH  = ACTION_BUTTON_WIDTH * 2   // 120.dp
private const val OPEN_THRESHOLD_FRACTION = 0.4f
private const val FLING_VELOCITY_THRESHOLD = 300f           // dp/s para disparar fling

@Composable
fun SwipeableActionsContainer(
    isOpen: Boolean,
    onOpen: () -> Unit,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scope     = rememberCoroutineScope()
    val offsetAnim = remember { Animatable(0f) }

    // Cierra con spring cuando el padre lo solicita (otro item abierto)
    LaunchedEffect(isOpen) {
        if (!isOpen) offsetAnim.animateTo(
            targetValue    = 0f,
            animationSpec  = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness    = Spring.StiffnessMedium
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds()
    ) {
        // Botones fijos a la derecha — zona izquierda transparente
        Row(
            modifier              = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .width(ACTION_BUTTON_WIDTH)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        scope.launch {
                            offsetAnim.animateTo(
                                0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                            )
                        }
                        onClose()
                        onEdit()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar", tint = White)
            }
            Box(
                modifier = Modifier
                    .width(ACTION_BUTTON_WIDTH)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.error)
                    .clickable {
                        scope.launch {
                            offsetAnim.animateTo(
                                0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                            )
                        }
                        onClose()
                        onDelete()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar", tint = White)
            }
        }

        // Card: desplazamiento limitado a ACTION_TOTAL_WIDTH con fling + spring
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetAnim.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    val maxOffsetPx   = -ACTION_TOTAL_WIDTH.toPx()
                    val thresholdPx   = maxOffsetPx * OPEN_THRESHOLD_FRACTION
                    val velocityTracker = VelocityTracker()

                    detectHorizontalDragGestures(
                        onDragStart = { velocityTracker.resetTracking() },
                        onHorizontalDrag = { change, delta ->
                            velocityTracker.addPosition(change.uptimeMillis, change.position)
                            val newOffset = (offsetAnim.value + delta).coerceIn(maxOffsetPx, 0f)
                            scope.launch { offsetAnim.snapTo(newOffset) }
                        },
                        onDragEnd = {
                            // Velocidad del gesto: positiva = derecha, negativa = izquierda
                            val velocity = velocityTracker.calculateVelocity().x
                            val shouldOpen = when {
                                velocity < -FLING_VELOCITY_THRESHOLD -> true   // fling rápido a la izquierda
                                velocity >  FLING_VELOCITY_THRESHOLD -> false  // fling rápido a la derecha
                                else -> offsetAnim.value <= thresholdPx        // umbral de posición
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
                            scope.launch {
                                offsetAnim.animateTo(
                                    0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                                )
                                onClose()
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}
