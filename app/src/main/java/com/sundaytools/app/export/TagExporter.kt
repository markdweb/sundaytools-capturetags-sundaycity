package com.sundaytools.app.export

import android.content.Context
import androidx.core.content.FileProvider
import com.sundaytools.app.database.entity.PlayerTagEntity
import com.sundaytools.app.utils.DateUtils
import java.io.File
import java.io.FileWriter
import javax.inject.Inject
import javax.inject.Singleton

enum class ExportFormat { CSV, TXT }

@Singleton
class TagExporter @Inject constructor() {

    /**
     * Writes [tags] to a file in the app's cache "exports" directory and returns a
     * content:// Uri suitable for sharing via an Intent.ACTION_SEND.
     * Excel export is left as a natural extension point: swap the writer below for
     * an .xlsx writer (e.g. Apache POI) while keeping the same call signature.
     */
    fun export(context: Context, tags: List<PlayerTagEntity>, format: ExportFormat): android.net.Uri {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val fileName = "sundaytools_export_${System.currentTimeMillis()}.${format.name.lowercase()}"
        val file = File(exportDir, fileName)

        FileWriter(file).use { writer ->
            when (format) {
                ExportFormat.CSV -> writeCsv(writer, tags)
                ExportFormat.TXT -> writeTxt(writer, tags)
            }
        }

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun writeCsv(writer: FileWriter, tags: List<PlayerTagEntity>) {
        writer.append("Name,Tag,Date,Time\n")
        for (tag in tags) {
            val date = DateUtils.formatDayKeyForDisplay(tag.dayKey)
            val time = DateUtils.formatTime(tag.savedAtEpochMillis)
            writer.append("${escapeCsv(tag.name.orEmpty())},${escapeCsv(tag.tag)},${escapeCsv(date)},${escapeCsv(time)}\n")
        }
    }

    private fun writeTxt(writer: FileWriter, tags: List<PlayerTagEntity>) {
        for (tag in tags) {
            val date = DateUtils.formatDayKeyForDisplay(tag.dayKey)
            val time = DateUtils.formatTime(tag.savedAtEpochMillis)
            val label = if (tag.name != null) "${tag.name} - ${tag.tag}" else tag.tag
            writer.append("$label\t$date $time\n")
        }
    }

    private fun escapeCsv(value: String): String =
        if (value.contains(",") || value.contains("\"")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
}
