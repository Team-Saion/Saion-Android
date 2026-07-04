package com.saion.ds.component.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.theme.SaionTheme

enum class SaionTextAreaVariant {
    NONE,
    BOX,
}

@Composable
fun SaionTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: SaionTextAreaVariant = SaionTextAreaVariant.NONE,
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    minLines: Int = 4,
    maxLines: Int = 4,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val spec = variant.toTextAreaSpec()
    SaionInputField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = label.takeIf { spec.showsLabel },
        placeholder = placeholder,
        supportingText = supportingText.takeIf { spec.showsSupportingText },
        isError = isError,
        minLines = minLines,
        maxLines = maxLines,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        spec = spec,
    )
}

private fun SaionTextAreaVariant.toTextAreaSpec(): SaionInputSpec = when (this) {
    SaionTextAreaVariant.NONE -> SaionInputSpec(
        component = SaionInputComponent.TEXT_AREA,
        containerVariant = SaionInputContainerVariant.LINE,
        typography = SaionInputTypography.HEADING,
        textAlign = TextAlign.Center,
        clearButtonPolicy = SaionInputClearButtonPolicy.NEVER,
        showsLabel = false,
        showsSupportingText = false,
        hidesBorder = true,
    )

    SaionTextAreaVariant.BOX -> SaionInputSpec(
        component = SaionInputComponent.TEXT_AREA,
        containerVariant = SaionInputContainerVariant.BOX,
        typography = SaionInputTypography.TITLE,
        clearButtonPolicy = SaionInputClearButtonPolicy.NEVER,
        showsLabel = true,
        showsSupportingText = true,
    )
}

@Preview(showBackground = true, name = "None")
@Composable
private fun SaionTextAreaNoneEnabledPreview() {
    SaionTheme {
        SaionTextArea(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            variant = SaionTextAreaVariant.NONE,
            value = "",
            onValueChange = {},
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "기본 상태",
            minLines = 1,
            maxLines = 3,
        )
    }
}

@Preview(showBackground = true, name = "None Focused")
@Composable
private fun SaionTextAreaNoneFocusedPreview() {
    SaionTheme {
        SaionTextArea(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            variant = SaionTextAreaVariant.NONE,
            value = "",
            onValueChange = {},
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "기본 상태",
            minLines = 1,
            maxLines = 3,
            interactionSource = previewFocusedInteractionSource(),
        )
    }
}

@Preview(showBackground = true, name = "None Typing")
@Composable
private fun SaionTextAreaNoneTypingPreview() {
    SaionTheme {
        SaionTextArea(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            variant = SaionTextAreaVariant.NONE,
            value = "사이온\n텍스트",
            onValueChange = {},
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "기본 상태",
            minLines = 1,
            maxLines = 3,
            interactionSource = previewFocusedInteractionSource(),
        )
    }
}

@Preview(showBackground = true, name = "None Disabled")
@Composable
private fun SaionTextAreaNoneDisabledPreview() {
    SaionTheme {
        SaionTextArea(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            variant = SaionTextAreaVariant.NONE,
            value = "",
            onValueChange = {},
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "기본 상태",
            minLines = 1,
            maxLines = 3,
            enabled = false,
        )
    }
}

@Preview(showBackground = true, name = "Box Enabled")
@Composable
private fun SaionTextAreaBoxEnabledPreview() {
    SaionTheme {
        SaionTextArea(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            variant = SaionTextAreaVariant.BOX,
            value = "",
            onValueChange = {},
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "기본 상태",
        )
    }
}

@Preview(showBackground = true, name = "Box Focused")
@Composable
private fun SaionTextAreaBoxFocusedPreview() {
    SaionTheme {
        SaionTextArea(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            variant = SaionTextAreaVariant.BOX,
            value = "",
            onValueChange = {},
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "포커스 상태",
            interactionSource = previewFocusedInteractionSource(),
        )
    }
}

@Preview(showBackground = true, name = "Box Typing")
@Composable
private fun SaionTextAreaBoxTypingPreview() {
    SaionTheme {
        SaionTextArea(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            variant = SaionTextAreaVariant.BOX,
            value = "사이온\n텍스트",
            onValueChange = {},
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "입력 중 상태",
            interactionSource = previewFocusedInteractionSource(),
        )
    }
}

@Preview(showBackground = true, name = "Box Error")
@Composable
private fun SaionTextAreaBoxErrorPreview() {
    SaionTheme {
        SaionTextArea(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            variant = SaionTextAreaVariant.BOX,
            value = "사이온\n텍스트",
            onValueChange = {},
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "오류 상태",
            isError = true,
        )
    }
}

@Preview(showBackground = true, name = "Box Disabled")
@Composable
private fun SaionTextAreaBoxDisabledPreview() {
    SaionTheme {
        SaionTextArea(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            variant = SaionTextAreaVariant.BOX,
            value = "",
            onValueChange = {},
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "비활성 상태",
            enabled = false,
        )
    }
}
