package com.saion.ds.component.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaionBottomSheet(
    state: SheetState,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    sheetGesturesEnabled: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        sheetState = state,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false,
            shouldDismissOnClickOutside = false,
        ),
        sheetGesturesEnabled = sheetGesturesEnabled,
        onDismissRequest = onDismissRequest,
        containerColor = SaionTheme.colors.background.default,
        shape = SaionTheme.radius.container.xxLarge.toRoundedCornerShape(),
        dragHandle = null,
        modifier = modifier.padding(horizontal = 12.dp),
    ) {
        SaionBottomSheetDragHandle()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            content = content,
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SaionBottomSheetDragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 16.dp)
                .size(height = 4.dp, width = 44.dp)
                .background(color = Color(0xFFE8EAEE)),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun SaionBottomSheetPreview() {
    var showSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState()

    SaionTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            if (showSheet) {
                SaionBottomSheet(state = sheetState, onDismissRequest = { showSheet = false }) {
                    Box(modifier = Modifier.height(300.dp))
                }
            }
        }
    }
}
