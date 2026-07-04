package com.saion.ds.component.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.ButtonVariant
import com.saion.ds.component.button.SaionButton
import com.saion.ds.component.button.SaionButtonArea
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape

@Composable
internal fun SaionDialog(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    onDismiss: () -> Unit,
    buttonArea: @Composable () -> Unit,
) {
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        ),
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .clip(shape = SaionTheme.radius.container.xxLarge.toRoundedCornerShape())
                .background(color = SaionTheme.colors.background.default),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 16.dp)
                    .padding(horizontal = 20.dp),
            ) {
                Text(
                    text = title,
                    style = SaionTheme.typography.title1Strong,
                    color = SaionTheme.colors.label.default,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                )
                description?.let { text ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = text,
                        style = SaionTheme.typography.title3,
                        color = SaionTheme.colors.label.subtle,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                buttonArea()
            }
        }
    }
}

@Composable
fun SaionAlertDialog(
    title: String,
    buttonText: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    onClick: () -> Unit,
) {
    SaionDialog(
        title = title,
        description = description,
        modifier = modifier,
        onDismiss = {},
    ) {
        SaionButtonArea(
            mainButton = { modifier ->
                SaionButton(
                    text = buttonText,
                    modifier = modifier,
                    onClick = onClick,
                    size = ButtonSize.LARGE,
                    variant = ButtonVariant.NEUTRAL,
                )
            },
        )
    }
}

@Composable
fun SaionConfirmDialog(
    title: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    dismissButtonText: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    isDanger: Boolean = false,
) {
    SaionDialog(
        title = title,
        description = description,
        modifier = modifier,
        onDismiss = onDismiss,
    ) {
        SaionButtonArea(
            isVertical = false,
            mainButton = { modifier ->
                SaionButton(
                    text = dismissButtonText,
                    modifier = modifier,
                    onClick = onDismiss,
                    size = ButtonSize.LARGE,
                    variant = ButtonVariant.NEUTRAL,
                )
            },
            subButton = { modifier ->
                SaionButton(
                    text = confirmButtonText,
                    modifier = modifier,
                    onClick = onConfirm,
                    size = ButtonSize.LARGE,
                    variant = if (isDanger) ButtonVariant.DANGER else ButtonVariant.PRIMARY,
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionAlertDialogPreview() {
    SaionTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SaionAlertDialog(
                title = "Title",
                description = "Description",
                buttonText = "확인",
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionConfirmDialogPreview() {
    SaionTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SaionConfirmDialog(
                title = "Title",
                description = "Description",
                confirmButtonText = "확인",
                onConfirm = {},
                dismissButtonText = "취소",
                onDismiss = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionDangerConfirmDialogPreview() {
    SaionTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SaionConfirmDialog(
                title = "Title",
                description = "Description",
                confirmButtonText = "삭제",
                onConfirm = {},
                dismissButtonText = "취소",
                onDismiss = {},
                isDanger = true,
            )
        }
    }
}
