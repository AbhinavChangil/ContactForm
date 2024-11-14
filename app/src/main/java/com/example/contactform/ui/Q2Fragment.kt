package com.example.contactform.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.contactform.R
import com.example.contactform.StepValidation
import com.example.contactform.databinding.FragmentQ2Binding
import com.example.contactform.viewmodel.ContactFormViewModel

class Q2Fragment : Fragment(), StepValidation {

    private var _binding: FragmentQ2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactFormViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQ2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAge()

        // Set up navigation
        binding.previousButton.setOnClickListener {
            findNavController().navigate(R.id.action_q2Fragment_to_q1Fragment)
        }

        binding.nextButton.setOnClickListener {
            if (isValid()) {
                viewModel.updateAge(binding.ageInput.text.toString().toInt())
                findNavController().navigate(R.id.action_q2Fragment_to_q3Fragment)
            } else {
                binding.ageInput.error = getErrorMessage()
                Toast.makeText(requireContext(), "Please enter a valid age", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe the age value in the ViewModel and set it if available
        viewModel.formData.observe(viewLifecycleOwner) { formData ->
            formData.age?.let {
                binding.ageInput.setText(it.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun isValid(): Boolean {
        val ageText = binding.ageInput.text.toString()
        return ageText.isNotEmpty() && ageText.toIntOrNull() != null
    }

    private fun setupAge() {
        // This function is kept for any additional setup if needed
        if (isValid()) {
            viewModel.updateAge(binding.ageInput.text.toString().toInt())
        }
    }

    override fun getErrorMessage(): String {
        return "Please enter a valid age."
    }
}
