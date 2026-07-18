package com.saion.feature.mypage.impl.mypage.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saion.core.ui.component.SaionProfile
import com.saion.core.ui.ext.noRippleClickable
import com.saion.ds.theme.SaionTheme
import com.saion.feature.mypage.impl.R
import com.saion.feature.mypage.impl.mypage.viewmodel.MyPageState

@Composable
internal fun ProfileSection(
    uiState: MyPageState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.noRippleClickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        SaionProfile(
            nickname = uiState.nickname,
            textStyle = SaionTheme.typography.display2,
            imageUrl = uiState.profileImageUrl,
            avatarColorHex = uiState.avatarColorHex,
            isShowEdit = true,
            contentDescription = stringResource(R.string.mypage_profile_image_description),
            modifier = Modifier.size(80.dp),
        )
        Text(
            text = uiState.nickname,
            style = SaionTheme.typography.heading1,
            color = SaionTheme.colors.label.default,
            textAlign = TextAlign.Center,
        )
    }
}
