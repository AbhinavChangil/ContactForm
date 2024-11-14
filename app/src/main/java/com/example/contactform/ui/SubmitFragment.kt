package com.example.contactform.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.contactform.R
import com.example.contactform.data.model.ContactFormData
import com.example.contactform.databinding.FragmentSubmitBinding
import com.example.contactform.repository.FileRepository
import com.example.contactform.repository.LocationRepository
import com.example.contactform.viewmodel.ContactFormViewModel
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubmitFragment : Fragment() {
    private var _binding: FragmentSubmitBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ContactFormViewModel
    private lateinit var locationRepository: LocationRepository
    private lateinit var fileRepository: FileRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSubmitBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ContactFormViewModel::class.java)
        locationRepository = LocationRepository(requireContext())
        fileRepository = FileRepository(requireContext()) // Initialize FileRepository

        binding.previousButton.setOnClickListener {
            findNavController().navigate(R.id.action_submitFragment_to_q3Fragment)
        }

        binding.submitButton.setOnClickListener {
            showLoading()
            val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()).toString()
            viewModel.formData.value?.submissionTime = timeStamp
            fetchLocationAndSubmit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.formData.observe(viewLifecycleOwner) { formData ->
            binding.tvGender.text = when (formData.gender) {
                0 -> "Gender Not Selected"
                1 -> "Male"
                2 -> "Female"
                else -> "Other"
            }
            binding.tvAge.text = formData.age.toString()
            binding.imageView.setImageURI(formData.selfieUri?.toUri())
        }
    }

    private fun fetchLocationAndSubmit() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            hideLoading()
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            showLoading()
            locationRepository.getCurrentLocation(
                onLocationFetched = { latLong ->
                    viewModel.updateGpsLocation(latLong)
                    saveDataAndNavigate()
                    Toast.makeText(requireContext(), "Form Submitted Successfully", Toast.LENGTH_SHORT).show()
                },
                onError = { error ->
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    hideLoading()
                }
            )
        }
    }

    private fun saveDataAndNavigate() {
        val formData = viewModel.formData.value ?: return

        // Save the image as .png file
        val imageUri = formData.selfieUri?.toUri()
        val imagePath = imageUri?.let { fileRepository.saveImageAsPng(it, "selfie_") }

        // Use FileRepository to save the data as JSON
        fileRepository.saveDataAsJson(formData, "contact_form_data.json")

        // Prepare the intent and navigate to ResultActivity
        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra("gender", when(formData.gender){
                0 -> "Gender Not Selected"
                else -> formData.gender.toString()
            }
            )
            putExtra("age", formData.age.toString())
            putExtra("selfieUri", imageUri.toString())
            putExtra("audioFilePath", formData.audioFilePath)
            putExtra("gpsLocation", formData.gpsLocation)
            putExtra("submissionTime", formData.submissionTime)
            putExtra("jsonFilePath", File(requireContext().filesDir, "contact_form_data.json").absolutePath)
        }
        startActivity(intent)
        // Finish the current activity to prevent going back
        hideLoading()
        requireActivity().finish()
    }
    private fun showLoading() {
        // Show the ProgressBar and disable the submit button
        binding.progressBar.visibility = View.VISIBLE
        binding.submitButton.isEnabled = false
    }
    private fun hideLoading() {
        // Hide the ProgressBar and re-enable the submit button
        binding.progressBar.visibility = View.GONE
        binding.submitButton.isEnabled = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocationAndSubmit()
        } else {
            hideLoading()
            Toast.makeText(requireContext(), "Location permission is required to continue", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
