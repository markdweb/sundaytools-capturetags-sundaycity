package com.sundaytools.app.utils

object WarningMessageUtils {

    /** Fills {name} and {tag} placeholders in [template] with the scanned player's info. */
    fun buildBlacklistWarning(template: String, name: String?, tag: String): String =
        template
            .replace("{name}", name?.takeIf { it.isNotBlank() } ?: "Unknown")
            .replace("{tag}", tag)
}
