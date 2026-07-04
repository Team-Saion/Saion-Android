package com.saion.ds.component.feedback

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.saion.ds.theme.SaionTheme

private val SpinnerSize = 48.dp
private val SpinnerStrokeWidth = 6.dp
private const val SPINNER_SWEEP_ANGLE = 250f
private const val SPINNER_DURATION = 1000

@Composable
fun SaionSpinner(modifier: Modifier = Modifier) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        ),
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            RotatingTrackSpinner(
                modifier = Modifier.size(SpinnerSize),
                indicatorColor = SaionTheme.colors.line.strong,
                trackColor = SaionTheme.colors.line.subtle,
                strokeWidth = SpinnerStrokeWidth,
            )
        }
    }
}

@Composable
private fun RotatingTrackSpinner(
    modifier: Modifier = Modifier,
    indicatorColor: Color,
    trackColor: Color,
    strokeWidth: Dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "saion_spinner_rotation")
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = SPINNER_DURATION,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "saion_spinner_angle",
    )

    Canvas(modifier = modifier) {
        val stroke = Stroke(
            width = strokeWidth.toPx(),
            cap = StrokeCap.Round,
        )
        val diameter = size.minDimension - stroke.width
        val topLeft = Offset(
            x = (size.width - diameter) / 2f,
            y = (size.height - diameter) / 2f,
        )
        val arcSize = Size(width = diameter, height = diameter)

        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke,
        )

        drawArc(
            color = indicatorColor,
            startAngle = rotation.value,
            sweepAngle = SPINNER_SWEEP_ANGLE,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionSpinnerPreview() {
    SaionTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SaionSpinner()
        }
    }
}
