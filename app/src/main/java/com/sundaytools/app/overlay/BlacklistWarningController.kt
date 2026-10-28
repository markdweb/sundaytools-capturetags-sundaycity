package com.sundaytools.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.sundaytools.app.ui.theme.SundayToolsTheme

/**
 * Shown instead of the normal Save dialog when the scanned tag is already on
 * record from a previous scan (i.e. "blacklisted"). Gives a one-tap "Copy
 * Warning" button that copies a ready-to-paste message (built from the
 * user's template in Settings) so they don't have to look anything up.
 */
class BlacklistWarningController(
    private val context: Context,
    private val windowManager: WindowManager,
    lifecycleOwner: LifecycleOwner,
    viewModelStoreOwner: ViewModelStoreOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    private val playerName: String?,
    private val playerTag: String,
    private val onCopyWarning: () -> Unit,
    private val onDismiss: () -> Unit,
    private val darkTheme: Boolean
) {
    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        BubbleViewController.overlayWindowType(),
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.CENTER
        dimAmount = 0.5f
    }

    private val composeView = ComposeView(context).apply {
        setViewTreeLifecycleOwner(lifecycleOwner)
        setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        setContent {
            SundayToolsTheme(darkTheme = darkTheme) {
                BlacklistWarningContent(
                    name = playerName,
                    tag = playerTag,
                    onCopyWarning = onCopyWarning,
                    onClose = { dismiss() }
                )
            }
        }
    }

    fun show() {
        runCatching { windowManager.addView(composeView, layoutParams) }
    }

    fun dismiss() {
        runCatching { windowManager.removeView(composeView) }
        onDismiss()
    }
}

@Composable
private fun BlacklistWarningContent(
    name: String?,
    tag: String,
    onCopyWarning: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.padding(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "\u26A0 BLACKLISTED",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE94560)
            )
            Text(
                text = "This tag is already on record from a previous scan.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
            )
            if (name != null) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = tag,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            Button(
                onClick = onCopyWarning,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy Warning")
            }

            TextButton(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Close")
            }
        }
    }
}
