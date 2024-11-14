package com.example.contactform.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.contactform.R
import com.example.contactform.StepValidation
import com.example.contactform.databinding.ActivityMainBinding
import com.example.contactform.repository.AudioRepository
import com.example.contactform.repository.FileRepository
import com.example.contactform.viewmodel.ContactFormViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ContactFormViewModel
    private lateinit var audioRepository: AudioRepository
    private lateinit var fileRepository: FileRepository
    private lateinit var audioFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel and AudioRepository
        viewModel = ViewModelProvider(this).get(ContactFormViewModel::class.java)
        audioRepository = AudioRepository()
        fileRepository = FileRepository(this) // Initialize FileRepository
        audioFile = fileRepository.createAudioFile()

        requestAudioPermission() // Request audio permissions if needed

        // Initialize NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }



    private fun requestAudioPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), AUDIO_PERMISSION_REQUEST_CODE)
        } else {
            startAudioRecording()
        }
    }

    private fun startAudioRecording() {
//        audioFile = fileRepository.createAudioFile()
        audioRepository.startRecording(audioFile.absolutePath)
        viewModel.updateAudioFilePath(audioFile.toString()) // Save audio path in ViewModel
    }

    private fun stopAudioRecording() {
        audioRepository.stopRecording()
        viewModel.updateAudioFilePath(audioFile.toString()) // Save audio path in ViewModel
//        Toast.makeText(this, "Audio recording saved to ${audioFile.absolutePath}", Toast.LENGTH_SHORT).show()
    }

    // Override onStop to stop recording when leaving the activity
    override fun onStop() {
        super.onStop()
        stopAudioRecording()
    }



    companion object {
        const val AUDIO_PERMISSION_REQUEST_CODE = 1002
    }
}
