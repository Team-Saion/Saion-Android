package com.saion.ds.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaionBottomNavigation(content: @Composable RowScope.() -> Unit) {
    val shape = SaionTheme.radius.component.xxLarge.toRoundedCornerShape().copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = shape)
            .background(color = SaionTheme.colors.background.default)
            .border(
                width = 1.dp,
                color = SaionTheme.colors.line.subtle,
                shape = shape,
            )
            .padding(horizontal = 10.dp)
            .padding(top = 8.dp, bottom = 2.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        content = content,
    )
}

@Composable
fun RowScope.SaionBottomNavItem(
    imageVector: ImageVector,
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .weight(1f)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                indication = null,
                interactionSource = null,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) SaionTheme.colors.primary.strong else SaionTheme.colors.primary.subtle,
        )
        Text(
            text = title,
            style = SaionTheme.typography.caption2,
            color = if (isSelected) SaionTheme.colors.label.strong else SaionTheme.colors.label.disabled,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionBottomNavigationPreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    SaionTheme {
        Box(modifier = Modifier.padding(top = 20.dp)) {
            SaionBottomNavigation {
                repeat(3) {
                    SaionBottomNavItem(
                        imageVector = SaionIcons.Selection,
                        title = "Label",
                        isSelected = selectedIndex == it,
                        onClick = { selectedIndex = it },
                    )
                }
            }
        }
    }
}
