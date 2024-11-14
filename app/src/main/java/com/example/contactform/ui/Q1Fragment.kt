package com.example.contactform.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.contactform.R
import com.example.contactform.databinding.FragmentQ1Binding
import com.example.contactform.viewmodel.ContactFormViewModel
import java.io.File
import java.io.IOException

class Q1Fragment : Fragment() {

    private var _binding: FragmentQ1Binding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactFormViewModel by activityViewModels()
    private var genderOptionNumber = 0

    private var mediaRecorder: MediaRecorder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQ1Binding.inflate(inflater, container, false)

        // Navigate to the next fragment when the 'Next' button is clicked
        binding.nextButton.setOnClickListener {
            if (genderOptionNumber != 0) { // Ensure a valid gender is selected
                findNavController().navigate(R.id.action_q1Fragment_to_q2Fragment)
                viewModel.updateGender(genderOptionNumber) // Use genderOptionNumber directly
            } else {
                Toast.makeText(requireContext(), "Gender Not Selected", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_q1Fragment_to_q2Fragment)
                viewModel.updateGender(0) // Use genderOptionNumber directly
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupGenderDropdown()

        // Observe the ViewModel's selectedGenderOption to set the dropdown value when returning to this fragment
        viewModel.selectedGenderOption.observe(viewLifecycleOwner) { genderOption ->
            genderOption?.let {
                genderOptionNumber = it
                val genderOptions = arrayOf("Male", "Female", "Others")
                if (it in 1..genderOptions.size) {
                    binding.genderDropdown.setText(genderOptions[it - 1], false) // Sets the dropdown to the selected option
                }
            }
        }
    }

    private fun setupGenderDropdown() {
        val genderOptions = arrayOf("Male", "Female", "Others")

        // Setting up the adapter for the AutoCompleteTextView
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderOptions)
        binding.genderDropdown.setAdapter(adapter)

        // Handle item selection from the dropdown
        binding.genderDropdown.setOnItemClickListener { _, _, position, _ ->
            genderOptionNumber = position + 1
            viewModel.updateGenderOption(genderOptionNumber) // Update ViewModel with the selected gender option
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val AUDIO_PERMISSION_REQUEST_CODE = 1002
    }
}
