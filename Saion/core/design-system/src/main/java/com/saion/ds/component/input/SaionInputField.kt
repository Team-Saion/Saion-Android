package com.saion.ds.component.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saion.ds.component.button.IconButtonSize
import com.saion.ds.component.button.SaionIconButton
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme

@Immutable
internal data class SaionInputSpec(
    val component: SaionInputComponent,
    val containerVariant: SaionInputContainerVariant,
    val typography: SaionInputTypography,
    val textAlign: TextAlign = TextAlign.Start,
    val clearButtonPolicy: SaionInputClearButtonPolicy,
    val showsLabel: Boolean = true,
    val showsSupportingText: Boolean = true,
    val hidesBorder: Boolean = false,
)

internal enum class SaionInputComponent {
    TEXT_FIELD,
    TEXT_AREA,
}

internal enum class SaionInputContainerVariant {
    LINE,
    BOX,
}

internal enum class SaionInputTypography {
    HEADING,
    TITLE,
}

internal enum class SaionInputClearButtonPolicy {
    NEVER,
    WHEN_FOCUSED_WITH_VALUE,
}

@Composable
internal fun SaionInputField(
    value: String,
    onValueChange: (String) -> Unit,
    interactionSource: MutableInteractionSource,
    keyboardOptions: KeyboardOptions,
    spec: SaionInputSpec,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = 1,
) {
    val interactionFocused by interactionSource.collectIsFocusedAsState()
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length),
            ),
        )
    }
    var wasFocused by remember { mutableStateOf(interactionFocused) }
    val stateContext = rememberSaionInputStateContext(
        value = value,
        enabled = enabled,
        isError = isError,
        isFocused = interactionFocused,
        containerVariant = spec.containerVariant,
    )
    val state = rememberSaionInputState(stateContext)
    val colors = state.colors(stateContext)
    val metrics = spec.containerVariant.metrics()
    val showClear = spec.clearButtonPolicy.shouldShow(stateContext)
    val textStyle = spec.typography.resolve(component = spec.component)
    val cursorBrush = SolidColor(
        if (value.isEmpty()) {
            Color.Transparent
        } else {
            SaionTheme.colors.label.default
        },
    )

    LaunchedEffect(value, interactionFocused) {
        val selection = when {
            !wasFocused && interactionFocused -> TextRange(value.length)
            else -> textFieldValue.selection.constrainedTo(value.length)
        }

        if (textFieldValue.text != value || textFieldValue.selection != selection) {
            textFieldValue = textFieldValue.copy(
                text = value,
                selection = selection,
            )
        }

        wasFocused = interactionFocused
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { stateDescription = state.description },
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        label?.takeIf { spec.showsLabel && it.isNotBlank() }?.let { labelText ->
            Text(
                text = labelText,
                style = SaionTheme.typography.label1Subtle,
                color = colors.labelColor,
            )
        }

        BasicTextField(
            value = textFieldValue,
            onValueChange = { updatedValue ->
                textFieldValue = updatedValue
                if (updatedValue.text != value) {
                    onValueChange(updatedValue.text)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            minLines = minLines,
            maxLines = maxLines,
            singleLine = maxLines == 1,
            textStyle = textStyle.copy(color = colors.textColor, textAlign = spec.textAlign),
            keyboardOptions = keyboardOptions,
            cursorBrush = cursorBrush,
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                SaionInputDecoration(
                    component = spec.component,
                    metrics = metrics,
                    colors = colors,
                    placeholder = placeholder,
                    value = value,
                    textStyle = textStyle,
                    textAlign = spec.textAlign,
                    showClear = showClear,
                    hidesBorder = spec.hidesBorder,
                    onClearClick = { onValueChange("") },
                    containerVariant = spec.containerVariant,
                    innerTextField = innerTextField,
                )
            },
        )

        supportingText?.takeIf { spec.showsSupportingText && it.isNotBlank() }?.let { message ->
            Text(
                text = message,
                style = SaionTheme.typography.body3,
                color = colors.supportingTextColor,
            )
        }
    }
}

@Composable
private fun SaionInputDecoration(
    component: SaionInputComponent,
    metrics: TextFieldMetrics,
    colors: TextFieldColors,
    placeholder: String?,
    value: String,
    textStyle: TextStyle,
    textAlign: TextAlign,
    showClear: Boolean,
    hidesBorder: Boolean,
    onClearClick: () -> Unit,
    containerVariant: SaionInputContainerVariant,
    innerTextField: @Composable () -> Unit,
) {
    when (containerVariant) {
        SaionInputContainerVariant.LINE -> LineFieldContainer(
            metrics = metrics,
            colors = colors,
            placeholder = placeholder,
            value = value,
            textStyle = textStyle,
            textAlign = textAlign,
            showClear = showClear,
            hidesBorder = hidesBorder,
            onClearClick = onClearClick,
            innerTextField = innerTextField,
        )

        SaionInputContainerVariant.BOX -> BoxFieldContainer(
            component = component,
            metrics = metrics,
            colors = colors,
            placeholder = placeholder,
            value = value,
            textStyle = textStyle,
            textAlign = textAlign,
            showClear = showClear,
            hidesBorder = hidesBorder,
            onClearClick = onClearClick,
            innerTextField = innerTextField,
        )
    }
}

@Composable
private fun LineFieldContainer(
    metrics: TextFieldMetrics,
    colors: TextFieldColors,
    placeholder: String?,
    value: String,
    textStyle: TextStyle,
    textAlign: TextAlign,
    showClear: Boolean,
    hidesBorder: Boolean,
    onClearClick: () -> Unit,
    innerTextField: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = metrics.containerHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FieldTextContent(
                value = value,
                placeholder = placeholder,
                textStyle = textStyle,
                placeholderColor = colors.placeholderColor,
                textAlign = textAlign,
                innerTextField = innerTextField,
            )

            if (showClear) {
                Spacer(modifier = Modifier.width(12.dp))
                ClearButton(onClick = onClearClick)
            }
        }

        if (!hidesBorder) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(metrics.indicatorThickness)
                    .background(colors.borderColor),
            )
        }
    }
}

@Composable
private fun BoxFieldContainer(
    component: SaionInputComponent,
    metrics: TextFieldMetrics,
    colors: TextFieldColors,
    placeholder: String?,
    value: String,
    textStyle: TextStyle,
    textAlign: TextAlign,
    showClear: Boolean,
    hidesBorder: Boolean,
    onClearClick: () -> Unit,
    innerTextField: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(metrics.shape)
            .background(colors.containerColor)
            .border(
                width = if (hidesBorder) 0.dp else metrics.indicatorThickness,
                color = colors.borderColor,
                shape = metrics.shape,
            )
            .padding(
                start = 14.dp,
                top = if (component == SaionInputComponent.TEXT_AREA) 14.dp else 0.dp,
                end = if (component == SaionInputComponent.TEXT_AREA) 14.dp else 6.dp,
                bottom = if (component == SaionInputComponent.TEXT_AREA) 14.dp else 0.dp,
            )
            .heightIn(min = metrics.containerHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FieldTextContent(
            value = value,
            placeholder = placeholder,
            textStyle = textStyle,
            placeholderColor = colors.placeholderColor,
            textAlign = textAlign,
            innerTextField = innerTextField,
        )

        if (showClear) {
            Spacer(modifier = Modifier.width(12.dp))
            ClearButton(onClick = onClearClick)
        }
    }
}

@Composable
private fun RowScope.FieldTextContent(
    value: String,
    placeholder: String?,
    textStyle: TextStyle,
    placeholderColor: Color,
    textAlign: TextAlign,
    innerTextField: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.weight(1f),
        contentAlignment = when (textAlign) {
            TextAlign.Center -> Alignment.Center
            else -> Alignment.TopStart
        },
    ) {
        if (value.isEmpty() && !placeholder.isNullOrBlank()) {
            Text(
                text = placeholder,
                style = textStyle,
                color = placeholderColor,
                textAlign = textAlign,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        innerTextField()
    }
}

@Composable
private fun ClearButton(onClick: () -> Unit) {
    SaionIconButton(
        icon = SaionIcons.CloseFilled,
        size = IconButtonSize.MEDIUM,
        tint = SaionTheme.colors.primary.subtle,
        contentDescription = "Clear text",
        onClick = onClick,
    )
}

@Composable
private fun rememberSaionInputStateContext(
    value: String,
    enabled: Boolean,
    isError: Boolean,
    isFocused: Boolean,
    containerVariant: SaionInputContainerVariant,
): SaionInputStateContext = remember(value, enabled, isError, isFocused, containerVariant) {
    SaionInputStateContext(
        value = value,
        enabled = enabled,
        isError = isError,
        isFocused = isFocused,
        containerVariant = containerVariant,
    )
}

@Composable
private fun rememberSaionInputState(context: SaionInputStateContext): SaionInputState = remember(context) {
    SaionInputState.entries.first { state -> state.matches(context) }
}

@Composable
private fun SaionInputContainerVariant.metrics(): TextFieldMetrics = when (this) {
    SaionInputContainerVariant.LINE -> TextFieldMetrics(
        containerHeight = 44.dp,
        indicatorThickness = 1.dp,
        shape = RoundedCornerShape(0.dp),
    )

    SaionInputContainerVariant.BOX -> TextFieldMetrics(
        containerHeight = 52.dp,
        indicatorThickness = 1.dp,
        shape = RoundedCornerShape(SaionTheme.radius.component.xLarge),
    )
}

@Composable
private fun SaionInputTypography.resolve(component: SaionInputComponent): TextStyle = when (component) {
    SaionInputComponent.TEXT_FIELD -> when (this) {
        SaionInputTypography.HEADING -> SaionTheme.typography.heading1Subtle
        SaionInputTypography.TITLE -> SaionTheme.typography.title1Subtle
    }

    SaionInputComponent.TEXT_AREA -> when (this) {
        SaionInputTypography.HEADING -> SaionTheme.typography.heading1
        SaionInputTypography.TITLE -> SaionTheme.typography.body1
    }
}

private fun SaionInputClearButtonPolicy.shouldShow(context: SaionInputStateContext): Boolean = when (this) {
    SaionInputClearButtonPolicy.NEVER -> false
    SaionInputClearButtonPolicy.WHEN_FOCUSED_WITH_VALUE -> context.enabled && context.isFocused && context.hasValue
}

private data class TextFieldColors(
    val textColor: Color,
    val labelColor: Color,
    val placeholderColor: Color,
    val supportingTextColor: Color,
    val borderColor: Color,
    val containerColor: Color,
)

private data class TextFieldMetrics(
    val containerHeight: Dp,
    val indicatorThickness: Dp,
    val shape: RoundedCornerShape,
)

@Immutable
private sealed interface SaionInputState {
    val description: String

    fun matches(context: SaionInputStateContext): Boolean

    @Composable
    fun colors(context: SaionInputStateContext): TextFieldColors

    data object Disabled : SaionInputState {
        override val description: String = "disabled"

        override fun matches(context: SaionInputStateContext): Boolean = !context.enabled

        @Composable
        override fun colors(context: SaionInputStateContext): TextFieldColors {
            val colors = SaionTheme.colors
            return TextFieldColors(
                textColor = colors.label.disabled,
                labelColor = colors.label.subtle,
                placeholderColor = colors.label.subtle,
                supportingTextColor = colors.label.subtle,
                borderColor = if (context.containerVariant == SaionInputContainerVariant.BOX) {
                    colors.fill.disabled
                } else {
                    colors.line.strong
                },
                containerColor = if (context.containerVariant == SaionInputContainerVariant.LINE) {
                    Color.Transparent
                } else {
                    colors.fill.disabled
                },
            )
        }
    }

    data object Error : SaionInputState {
        override val description: String = "error"

        override fun matches(context: SaionInputStateContext): Boolean = context.isError

        @Composable
        override fun colors(context: SaionInputStateContext): TextFieldColors {
            val colors = SaionTheme.colors
            return TextFieldColors(
                textColor = colors.label.strong,
                labelColor = colors.status.negative.default,
                placeholderColor = colors.label.disabled,
                supportingTextColor = colors.status.negative.default,
                borderColor = colors.status.negative.default,
                containerColor = if (context.containerVariant == SaionInputContainerVariant.LINE) {
                    Color.Transparent
                } else {
                    colors.background.subtle
                },
            )
        }
    }

    data object Focused : SaionInputState {
        override val description: String = "focused"

        override fun matches(context: SaionInputStateContext): Boolean = context.isFocused && !context.hasValue

        @Composable
        override fun colors(context: SaionInputStateContext): TextFieldColors {
            val colors = SaionTheme.colors
            return TextFieldColors(
                textColor = colors.label.strong,
                labelColor = colors.label.strong,
                placeholderColor = colors.label.disabled,
                supportingTextColor = colors.label.subtle,
                borderColor = colors.label.strong,
                containerColor = if (context.containerVariant == SaionInputContainerVariant.LINE) {
                    Color.Transparent
                } else {
                    colors.background.subtle
                },
            )
        }
    }

    data object Typing : SaionInputState {
        override val description: String = "typing"

        override fun matches(context: SaionInputStateContext): Boolean = context.isFocused && context.hasValue

        @Composable
        override fun colors(context: SaionInputStateContext): TextFieldColors {
            val colors = SaionTheme.colors
            return TextFieldColors(
                textColor = colors.label.strong,
                labelColor = colors.label.strong,
                placeholderColor = colors.label.disabled,
                supportingTextColor = colors.label.subtle,
                borderColor = colors.label.strong,
                containerColor = if (context.containerVariant == SaionInputContainerVariant.LINE) {
                    Color.Transparent
                } else {
                    colors.background.subtle
                },
            )
        }
    }

    data object Typed : SaionInputState {
        override val description: String = "typed"

        override fun matches(context: SaionInputStateContext): Boolean = context.hasValue

        @Composable
        override fun colors(context: SaionInputStateContext): TextFieldColors {
            val colors = SaionTheme.colors
            return TextFieldColors(
                textColor = colors.label.strong,
                labelColor = colors.label.strong,
                placeholderColor = colors.label.disabled,
                supportingTextColor = colors.label.subtle,
                borderColor = when (context.containerVariant) {
                    SaionInputContainerVariant.LINE -> colors.line.subtle
                    SaionInputContainerVariant.BOX -> colors.line.default
                },
                containerColor = if (context.containerVariant == SaionInputContainerVariant.LINE) {
                    Color.Transparent
                } else {
                    colors.background.subtle
                },
            )
        }
    }

    data object Enabled : SaionInputState {
        override val description: String = "enabled"

        override fun matches(context: SaionInputStateContext): Boolean = true

        @Composable
        override fun colors(context: SaionInputStateContext): TextFieldColors {
            val colors = SaionTheme.colors
            return TextFieldColors(
                textColor = colors.label.strong,
                labelColor = colors.label.strong,
                placeholderColor = colors.label.disabled,
                supportingTextColor = colors.label.subtle,
                borderColor = when (context.containerVariant) {
                    SaionInputContainerVariant.LINE -> colors.line.subtle
                    SaionInputContainerVariant.BOX -> colors.line.default
                },
                containerColor = if (context.containerVariant == SaionInputContainerVariant.LINE) {
                    Color.Transparent
                } else {
                    colors.background.subtle
                },
            )
        }
    }

    companion object {
        val entries: List<SaionInputState> = listOf(
            Disabled,
            Error,
            Focused,
            Typing,
            Typed,
            Enabled,
        )
    }
}

@Immutable
private data class SaionInputStateContext(
    val value: String,
    val enabled: Boolean,
    val isError: Boolean,
    val isFocused: Boolean,
    val containerVariant: SaionInputContainerVariant,
) {
    val hasValue: Boolean
        get() = value.isNotEmpty()
}

@Composable
internal fun previewFocusedInteractionSource(): MutableInteractionSource = remember {
    MutableInteractionSource().also { interactionSource ->
        interactionSource.tryEmit(FocusInteraction.Focus())
    }
}

private fun TextRange.constrainedTo(textLength: Int): TextRange = TextRange(
    start = start.coerceIn(0, textLength),
    end = end.coerceIn(0, textLength),
)
