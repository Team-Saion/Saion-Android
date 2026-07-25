package com.saion.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.saion.app.invitation.PendingInvitationLinkStore
import com.saion.app.notification.FcmNotificationNavigationParser
import com.saion.app.ui.SaionApp
import com.saion.app.viewmodel.AppViewModel
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.store.PendingNotificationNavigationStore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.jvm.JvmSuppressWildcards
import kotlinx.collections.immutable.toImmutableSet

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var rootEntryBuilders: Set<@JvmSuppressWildcards NavEntryBuilder<AppNavKey>>

    @Inject
    lateinit var pendingInvitationLinkStore: PendingInvitationLinkStore

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        configureEdgeToEdge()
        setContent {
            SaionApp(
                appViewModel = appViewModel,
                pendingInvitationLinkStore = pendingInvitationLinkStore,
                rootEntryBuilders = rootEntryBuilders.toImmutableSet(),
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun configureEdgeToEdge() {
        val scrim = SystemBarStyle.light(
            scrim = Color.Transparent.toArgb(),
            darkScrim = Color.Transparent.toArgb(),
        )
        enableEdgeToEdge(statusBarStyle = scrim, navigationBarStyle = scrim)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.toInvitationToken()?.let(appViewModel::savePendingInvitation)
        intent?.toNotificationPayload()
            ?.let(FcmNotificationNavigationParser::parse)
            ?.let(PendingNotificationNavigationStore::save)
    }

    private fun Uri.toInvitationToken(): String? {
        if (scheme != BuildConfig.KAKAO_SHARE_SCHEME) return null
        if (host != "kakaolink") return null
        if (getQueryParameter("action") != "invite") return null
        return getQueryParameter("token")?.takeIf { it.isNotBlank() }
    }

    private fun Intent.toNotificationPayload(): Map<String, String>? {
        val extras = extras ?: return null
        val payload = extras.keySet()
            .mapNotNull { key ->
                extras.getString(key)?.let { value -> key to value }
            }
            .toMap()

        return payload.takeIf { it.isNotEmpty() }
    }
}
