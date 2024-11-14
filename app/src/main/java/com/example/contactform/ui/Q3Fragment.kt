package com.example.contactform.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.contactform.R
import com.example.contactform.databinding.FragmentQ3Binding
import com.example.contactform.viewmodel.ContactFormViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Q3Fragment : Fragment() {

    private var _binding: FragmentQ3Binding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactFormViewModel by activityViewModels()

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQ3Binding.inflate(inflater, container, false)

        binding.previousButton.setOnClickListener {
            findNavController().navigate(R.id.action_q3Fragment_to_q2Fragment)
        }

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_q3Fragment_to_submitFragment)
        }

        binding.takePhotoButton.setOnClickListener {
            openFrontCamera()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Observe the selfie URI in the ViewModel and display it if available
        viewModel.formData.observe(viewLifecycleOwner) { formData ->
            formData.selfieUri?.let {
                Log.d("Q3Fragment", "Selfie URI: $it")
                binding.imageView.setImageURI(Uri.parse(it))
            }
        }
    }

    private fun openFrontCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            try {
                photoFile = createImageFile()
                photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error creating file", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Camera app not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (::photoUri.isInitialized && photoFile.exists()) {
                binding.imageView.setImageURI(photoUri)
                viewModel.updateSelfieUri(photoUri.toString())
            } else {
                Toast.makeText(requireContext(), "Error displaying photo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Photo capture canceled", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(null)
        return File.createTempFile("img_${timeStamp}_", ".png", storageDir).apply {
            Log.d("Q3Fragment", "Photo file created at: ${this.absolutePath}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
