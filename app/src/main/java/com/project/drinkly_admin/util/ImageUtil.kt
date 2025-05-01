package com.project.drinkly_admin.util

import android.content.Context
import java.io.File

object ImageUtil {
    fun copyRawToFile(context: Context, rawResId: Int, fileName: String): File {
        val inputStream = context.resources.openRawResource(rawResId)
        val tempFile = File(context.cacheDir, fileName)

        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }

}