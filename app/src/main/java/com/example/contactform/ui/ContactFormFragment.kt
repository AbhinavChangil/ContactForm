//package com.example.contactform.ui
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.Location
//import android.location.LocationListener
//import android.location.LocationManager
//import android.media.MediaRecorder
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import com.example.contactform.R
//import com.example.contactform.StepValidation
//import com.example.contactform.data.model.ContactFormData
//import com.example.contactform.databinding.FragmentContactFormBinding
//import com.example.contactform.viewmodel.ContactFormViewModel
//import org.json.JSONObject
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.text.SimpleDateFormat
//import java.util.*
//
//class ContactFormFragment : Fragment() {
//
//    private var _binding: FragmentContactFormBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var viewModel: ContactFormViewModel
//    private var currentPage = 1
//    private var locationManager: LocationManager? = null
//    private var mediaRecorder: MediaRecorder? = null
//    private lateinit var audioFile: File
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        _binding = FragmentContactFormBinding.inflate(inflater, container, false)
//        viewModel = ViewModelProvider(requireActivity()).get(ContactFormViewModel::class.java)
//        locationManager = requireContext().getSystemService(LocationManager::class.java)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        setupStepper()
//        setupButtonListeners()
//        displayCurrentFragment()
//        requestAudioPermission() // Request permission to start audio recording
//    }
//
//    private fun setupStepper() {
//        updateStepper()
//    }
//
//    private fun setupButtonListeners() {
//        binding.nextButton.setOnClickListener {
//            if (validateCurrentStep()) {
//                if (currentPage < 4) {
//                    currentPage++
//                    updateStepper()
//                    displayCurrentFragment()
//                }
//            }
//        }
//
//        binding.previousButton.setOnClickListener {
//            if (currentPage > 1) {
//                currentPage--
//                updateStepper()
//                displayCurrentFragment()
//            }
//        }
//
//        binding.submitButton.setOnClickListener {
//            if (validateCurrentStep()) {
//                captureLocationAndTimestamp()
//                stopAudioRecording() // Stop recording audio upon submission
//            }
//        }
//    }
//
//    private fun requestAudioPermission() {
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), AUDIO_PERMISSION_REQUEST_CODE)
//        } else {
//            startAudioRecording()
//        }
//    }
//
//    private fun startAudioRecording() {
//        audioFile = File(requireContext().externalCacheDir, "audio_recording.wav")
//        mediaRecorder = MediaRecorder().apply {
//            try {
//                setAudioSource(MediaRecorder.AudioSource.MIC)
//                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//                setOutputFile(audioFile.absolutePath)
//                prepare()
//                start()
//                viewModel.updateAudioFilePath(audioFile.absolutePath) // Save audio path in ViewModel
//            } catch (e: IOException) {
//                e.printStackTrace()
//                Toast.makeText(requireContext(), "Audio recording failed", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun stopAudioRecording() {
//        mediaRecorder?.apply {
//            try {
//                stop()
//                release()
//            } catch (e: IOException) {
//                e.printStackTrace()
//                Toast.makeText(requireContext(), "Failed to stop audio recording", Toast.LENGTH_SHORT).show()
//            }
//        }
//        mediaRecorder = null
//    }
//
//    private fun validateCurrentStep(): Boolean {
//        val currentFragment = childFragmentManager.findFragmentById(R.id.fragmentContainer)
//        return if (currentFragment is StepValidation) {
//            if (currentFragment.isValid()) {
//                true
//            } else {
//                Toast.makeText(requireContext(), currentFragment.getErrorMessage(), Toast.LENGTH_SHORT).show()
//                false
//            }
//        } else {
//            true
//        }
//    }
//
//    private fun captureLocationAndTimestamp() {
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
//        } else {
//            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
//        }
//
//        viewModel.updateSubmissionTime()
//    }
//
//    private val locationListener = LocationListener { location: Location ->
//        val gpsLocation = "${location.latitude},${location.longitude}"
//        viewModel.updateGpsLocation(gpsLocation)
//        saveDataAsJson() // Save data after capturing location
//    }
//
//    private fun saveDataAsJson() {
//        val formData = viewModel.formData.value ?: ContactFormData()
//        val json = JSONObject().apply {
//            put("gender", formData.gender)
//            put("age", formData.age)
//            put("selfieUri", formData.selfieUri)
//            put("audioFilePath", formData.audioFilePath)
//            put("gpsLocation", formData.gpsLocation)
//            put("submissionTime", formData.submissionTime)
//        }
//
//        val file = File(requireContext().filesDir, "contact_form_data.json")
//        FileOutputStream(file).use { it.write(json.toString().toByteArray()) }
//
//        val intent = Intent(requireContext(), ResultActivity::class.java)
//        intent.putExtra("jsonFilePath", file.absolutePath)
//        startActivity(intent)
//    }
//
//    private fun updateStepper() {
//        binding.step1Icon.setBackgroundResource(if (currentPage >= 1) R.drawable.circle_step_complete else R.drawable.circle_step_incomplete)
//        binding.step2Icon.setBackgroundResource(if (currentPage >= 2) R.drawable.circle_step_complete else R.drawable.circle_step_incomplete)
//        binding.step3Icon.setBackgroundResource(if (currentPage >= 3) R.drawable.circle_step_complete else R.drawable.circle_step_incomplete)
//        binding.step4Icon.setBackgroundResource(if (currentPage >= 4) R.drawable.circle_step_complete else R.drawable.circle_step_incomplete)
//
//        binding.pageTitle.text = when (currentPage) {
//            1 -> "Select Gender"
//            2 -> "Enter Age"
//            3 -> "Upload Selfie"
//            4 -> "Submit Form"
//            else -> ""
//        }
//
//        binding.previousButton.visibility = if (currentPage == 1) View.INVISIBLE else View.VISIBLE
//        binding.nextButton.visibility = if (currentPage == 4) View.GONE else View.VISIBLE
//        binding.submitButton.visibility = if (currentPage == 4) View.VISIBLE else View.GONE
//    }
//
//    private fun displayCurrentFragment() {
//        val fragment = when (currentPage) {
//            1 -> Q1Fragment()
//            2 -> Q2Fragment()
//            3 -> Q3Fragment()
//            4 -> SubmitFragment()
//            else -> Q1Fragment()
//        }
//
//        childFragmentManager.beginTransaction()
//            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//            .replace(R.id.fragmentContainer, fragment)
//            .commit()
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            captureLocationAndTimestamp()
//        } else if (requestCode == AUDIO_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            startAudioRecording()
//        } else {
//            Toast.makeText(requireContext(), "Permission required", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//        locationManager?.removeUpdates(locationListener)
//        stopAudioRecording() // Ensure recording stops if fragment is destroyed
//    }
//
//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
//        private const val AUDIO_PERMISSION_REQUEST_CODE = 1002
//    }
//}
