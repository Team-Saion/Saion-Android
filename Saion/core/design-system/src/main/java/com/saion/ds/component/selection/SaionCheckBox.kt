package com.saion.ds.component.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme

enum class CheckBoxVariant {
    LINE,
    CIRCLE,
}

enum class CheckBoxSize {
    LARGE,
    MEDIUM,
    SMALL,
}

@Composable
fun SaionCheckBox(
    isChecked: Boolean,
    onCheckedChange: (isChecked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    variant: CheckBoxVariant = CheckBoxVariant.LINE,
    size: CheckBoxSize = CheckBoxSize.MEDIUM,
    enabled: Boolean = true,
) {
    Icon(
        imageVector = when (variant) {
            CheckBoxVariant.LINE -> SaionIcons.Check
            CheckBoxVariant.CIRCLE -> SaionIcons.CheckFilled
        },
        contentDescription = contentDescription,
        tint = if (isChecked) SaionTheme.colors.primary.strong else SaionTheme.colors.line.strong,
        modifier = modifier
            .size(size.size)
            .toggleable(
                value = isChecked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Checkbox,
                indication = null,
                interactionSource = null,
            ),
    )
}

private val CheckBoxSize.size: Dp
    get() = when (this) {
        CheckBoxSize.LARGE -> 24.dp
        CheckBoxSize.MEDIUM -> 20.dp
        CheckBoxSize.SMALL -> 16.dp
    }

@Preview(showBackground = true)
@Composable
private fun SaionCheckBoxLinePreview() {
    SaionTheme {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CheckBoxSize.entries.forEach { size ->
                var isChecked by remember { mutableStateOf(true) }
                SaionCheckBox(
                    isChecked = isChecked,
                    onCheckedChange = { isChecked = it },
                    size = size,
                    variant = CheckBoxVariant.LINE,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionCheckBoxCirclePreview() {
    SaionTheme {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CheckBoxSize.entries.forEach { size ->
                var isChecked by remember { mutableStateOf(true) }
                SaionCheckBox(
                    isChecked = isChecked,
                    onCheckedChange = { isChecked = it },
                    size = size,
                    variant = CheckBoxVariant.CIRCLE,
                )
            }
        }
    }
}
