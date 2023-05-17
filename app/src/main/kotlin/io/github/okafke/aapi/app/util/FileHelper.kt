package io.github.okafke.aapi.app.util

import android.content.Context
import java.io.*

object FileHelper {
    fun read(context: Context, fileName: String): String? {
        return try {
            val bufferedReader = BufferedReader(InputStreamReader(context.openFileInput(fileName)))
            with(bufferedReader) {
                val sb = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                sb.toString()
            }
        } catch (fileNotFound: FileNotFoundException) {
            null
        } catch (ioException: IOException) {
            null
        }
    }

    fun create(context: Context, fileName: String, jsonString: String?): Boolean {
        return try {
            val fos: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            if (jsonString != null) {
                fos.write(jsonString.toByteArray())
            }

            fos.close()
            true
        } catch (fileNotFound: FileNotFoundException) {
            false
        } catch (ioException: IOException) {
            false
        }
    }

    fun isFilePresent(context: Context, fileName: String): Boolean {
        val path: String = context.filesDir.absolutePath + "/" + fileName
        val file = File(path)
        return file.exists()
    }

    fun deleteFile(context: Context, fileName: String): Boolean {
        val path: String = context.filesDir.absolutePath + "/" + fileName
        val file = File(path)
        return file.delete()
    }

}