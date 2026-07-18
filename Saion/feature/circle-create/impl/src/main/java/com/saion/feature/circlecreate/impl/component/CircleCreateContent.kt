package com.saion.feature.circlecreate.impl.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.component.input.SaionTextArea
import com.saion.ds.component.input.SaionTextAreaVariant
import com.saion.ds.theme.SaionTheme
import com.saion.feature.circlecreate.impl.R

@Composable
internal fun CircleCreateContent(
    name: String,
    placeholder: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.circle_create_heading),
            style = SaionTheme.typography.title2,
            color = SaionTheme.colors.label.default,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SaionTextArea(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            variant = SaionTextAreaVariant.NONE,
            placeholder = placeholder,
            minLines = 1,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CircleCreateContentPreview() {
    SaionTheme {
        CircleCreateContent(
            name = "",
            placeholder = "써클 이름",
            onNameChange = {},
        )
    }
}
