package com.saion.feature.auth.impl.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.feature.auth.impl.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel(),
) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(loginViewModel) {
        loginViewModel.uiEffect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateNext -> onLoginSuccess()
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        onLoginClick = {
            loginViewModel.login(context)
        },
        onDismissError = {
            loginViewModel.dispatch(LoginIntent.DismissError)
        },
        modifier = modifier,
    )
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    onLoginClick: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(R.string.login_title))
        Text(
            text = stringResource(R.string.login_subtitle),
            modifier = Modifier.padding(top = 8.dp),
        )
        Button(
            onClick = onLoginClick,
            enabled = !uiState.isLoading,
            modifier = Modifier.padding(top = 24.dp),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(18.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Text(text = stringResource(R.string.login_with_kakao))
            }
        }
        uiState.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                modifier = Modifier.padding(top = 16.dp),
            )
            TextButton(
                onClick = onDismissError,
                modifier = Modifier.padding(top = 4.dp),
            ) {
                Text(text = "확인")
            }
        }
    }
}
