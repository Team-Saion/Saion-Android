package com.saion.ds.component.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.theme.SaionTheme

enum class InputFieldVariant {
    LINE,
    BOX,
}

@Composable
fun SaionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: InputFieldVariant = InputFieldVariant.LINE,
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    SaionInputField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        isError = isError,
        minLines = 1,
        maxLines = 1,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        spec = variant.toTextFieldSpec(),
    )
}

private fun InputFieldVariant.toTextFieldSpec(): SaionInputSpec = when (this) {
    InputFieldVariant.LINE -> SaionInputSpec(
        component = SaionInputComponent.TEXT_FIELD,
        containerVariant = SaionInputContainerVariant.LINE,
        typography = SaionInputTypography.HEADING,
        clearButtonPolicy = SaionInputClearButtonPolicy.WHEN_FOCUSED_WITH_VALUE,
    )

    InputFieldVariant.BOX -> SaionInputSpec(
        component = SaionInputComponent.TEXT_FIELD,
        containerVariant = SaionInputContainerVariant.BOX,
        typography = SaionInputTypography.TITLE,
        clearButtonPolicy = SaionInputClearButtonPolicy.WHEN_FOCUSED_WITH_VALUE,
    )
}

@Preview(showBackground = true, name = "Line Enabled")
@Composable
private fun SaionTextFieldLineEnabledPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "",
            onValueChange = {},
            variant = InputFieldVariant.LINE,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "기본 상태",
        )
    }
}

@Preview(showBackground = true, name = "Line Focused")
@Composable
private fun SaionTextFieldLineFocusedPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "",
            onValueChange = {},
            variant = InputFieldVariant.LINE,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "포커스 상태",
            interactionSource = previewFocusedInteractionSource(),
        )
    }
}

@Preview(showBackground = true, name = "Line Typing")
@Composable
private fun SaionTextFieldLineTypingPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "사이온",
            onValueChange = {},
            variant = InputFieldVariant.LINE,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "입력 중 상태",
            interactionSource = previewFocusedInteractionSource(),
        )
    }
}

@Preview(showBackground = true, name = "Line Typed")
@Composable
private fun SaionTextFieldLineTypedPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "사이온",
            onValueChange = {},
            variant = InputFieldVariant.LINE,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "입력 완료 상태",
        )
    }
}

@Preview(showBackground = true, name = "Line Error")
@Composable
private fun SaionTextFieldLineErrorPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "사이온",
            onValueChange = {},
            variant = InputFieldVariant.LINE,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "오류 상태",
            isError = true,
        )
    }
}

@Preview(showBackground = true, name = "Line Disabled")
@Composable
private fun SaionTextFieldLineDisabledPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "",
            onValueChange = {},
            variant = InputFieldVariant.LINE,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "비활성 상태",
            enabled = false,
        )
    }
}

@Preview(showBackground = true, name = "Box Enabled")
@Composable
private fun SaionTextFieldBoxEnabledPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "",
            onValueChange = {},
            variant = InputFieldVariant.BOX,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "기본 상태",
        )
    }
}

@Preview(showBackground = true, name = "Box Focused")
@Composable
private fun SaionTextFieldBoxFocusedPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "",
            onValueChange = {},
            variant = InputFieldVariant.BOX,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "포커스 상태",
            interactionSource = previewFocusedInteractionSource(),
        )
    }
}

@Preview(showBackground = true, name = "Box Typing")
@Composable
private fun SaionTextFieldBoxTypingPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "사이온",
            onValueChange = {},
            variant = InputFieldVariant.BOX,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "입력 중 상태",
            interactionSource = previewFocusedInteractionSource(),
        )
    }
}

@Preview(showBackground = true, name = "Box Typed")
@Composable
private fun SaionTextFieldBoxTypedPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "사이온",
            onValueChange = {},
            variant = InputFieldVariant.BOX,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "입력 완료 상태",
        )
    }
}

@Preview(showBackground = true, name = "Box Error")
@Composable
private fun SaionTextFieldBoxErrorPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "사이온",
            onValueChange = {},
            variant = InputFieldVariant.BOX,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "오류 상태",
            isError = true,
        )
    }
}

@Preview(showBackground = true, name = "Box Disabled")
@Composable
private fun SaionTextFieldBoxDisabledPreview() {
    SaionTheme {
        SaionTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = "",
            onValueChange = {},
            variant = InputFieldVariant.BOX,
            label = "제목",
            placeholder = "입력해 주세요",
            supportingText = "비활성 상태",
            enabled = false,
        )
    }
}
