package com.example.contactform.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contactform.data.model.ContactFormData
import java.text.SimpleDateFormat
import java.util.*

class ContactFormViewModel : ViewModel() {
    val formData = MutableLiveData(ContactFormData())
    val _formData: LiveData<ContactFormData> get() = formData

    // Add a new LiveData for gender option
    private val _selectedGenderOption = MutableLiveData<Int>()
    val selectedGenderOption: LiveData<Int> get() = _selectedGenderOption

    // Update gender option
    fun updateGenderOption(option: Int) {
        _selectedGenderOption.value = option
    }

    fun updateGender(genderOption: Int) {
        formData.value = formData.value?.apply { this.gender = genderOption }
    }

    fun updateAge(age: Int) {
        formData.value = formData.value?.apply { this.age = age }
    }

    fun updateSelfieUri(uri: String) {
        formData.value = formData.value?.apply { this.selfieUri = uri }
    }

    fun updateAudioFilePath(path: String) {
        formData.value = formData.value?.apply { this.audioFilePath = path }
    }

    fun updateGpsLocation(location: String) {
        formData.value = formData.value?.apply { this.gpsLocation = location }
    }

    fun updateSubmissionTime() {
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        formData.value = formData.value?.apply { this.submissionTime = currentTime }
    }
}
