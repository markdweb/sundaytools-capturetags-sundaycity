package com.sundaytools.app.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtils {

    /** Copies [text] to the system clipboard, labeling it for easy identification in clipboard managers. */
    fun copyToClipboard(context: Context, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("SundayTools Player Tag", text)
        clipboardManager.setPrimaryClip(clip)
    }
}
