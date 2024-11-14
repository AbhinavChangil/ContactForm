// FileRepository.kt
package com.example.contactform.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.contactform.data.model.ContactFormData
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileRepository(private val context: Context) {

    // Function to save JSON data
    fun saveDataAsJson(data: ContactFormData, filename: String) {
        val jsonString = Gson().toJson(data)
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    // Function to save an image as PNG
    fun saveImageAsPng(imageUri: Uri, filename: String): String? {
        val bitmap = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        } ?: return null
        val storageDir = File(context.getExternalFilesDir(null), "ContactForm/Images")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(storageDir, "$filename$timeStamp.png")

        try {
            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            return imageFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    // Function to create an audio file
    fun createAudioFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(context.getExternalFilesDir(null), "ContactForm/Audio")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        return File(storageDir, "AUDIO_$timeStamp.wav").apply {
            Log.d("AudioRecording", "Audio file created: $absolutePath")
        }
    }
}
