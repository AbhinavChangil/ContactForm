package com.example.contactform.ui
import android.media.MediaPlayer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.contactform.R
import com.example.contactform.databinding.ActivityResultBinding
import com.example.contactform.viewmodel.ContactFormViewModel
import java.io.File

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: ContactFormViewModel
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            columnQ1.text = intent.getStringExtra("gender")
            columnQ2.text = intent.getStringExtra("age")

            val selfieUri = intent.getStringExtra("selfieUri")
            columnQ3.setImageURI(selfieUri?.toUri())

            val audioFilePath = intent.getStringExtra("audioFilePath")
            columnImagePath.text = selfieUri
            if (audioFilePath != null) {
                columnAudioPath.text = audioFilePath
            }

            if (audioFilePath != null) {
                playAudioButton.text = "Play Audio"  // Initial text for play button
                playAudioButton.setOnClickListener {
                    togglePlayPause(Uri.parse(audioFilePath))
                }
            }

            columnGPS.text = intent.getStringExtra("gpsLocation")
            columnTimestamp.text = intent.getStringExtra("submissionTime")

            againButton.setOnClickListener {
                startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            }
        }

        mediaPlayer?.setOnErrorListener { _, what, extra ->
            Log.e("MediaPlayer", "Error occurred, what: $what, extra: $extra")
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show()
            true
        }


    }

    // Function to toggle between play and pause
    private fun togglePlayPause(audioUri: Uri) {
        if (isPlaying) {
            pauseAudio()  // Pause if currently playing
        } else {
            playAudio(audioUri)  // Play if currently paused
        }
    }



    // Function to start or resume audio playback
    private fun playAudio(audioUri: Uri) {
        if (audioUri != null && File(audioUri.path!!).exists()) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@ResultActivity, audioUri)
                prepare()  // This is where the error occurs
                start()
                setOnCompletionListener {
                    stopAudio()  // Call stopAudio when audio completes
                }
            }
            isPlaying = true
            binding.playAudioButton.text = "Pause Audio"
        } else {
            Toast.makeText(this, "Audio file not found or URI is invalid", Toast.LENGTH_SHORT).show()
        }
    }


    // Function to pause audio playback
    private fun pauseAudio() {
        mediaPlayer?.pause()
        isPlaying = false
        binding.playAudioButton.text = "Play Audio"
    }

    // Function to stop audio playback completely
    private fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
        binding.playAudioButton.text = "Play Audio"
    }
}
