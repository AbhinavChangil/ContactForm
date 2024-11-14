package com.example.contactform.data.model

data class ContactFormData(
    var gender: Int? = null,
    var age: Int? = null,
    var selfieUri: String? = null,
    var audioFilePath: String? = null,
    var gpsLocation: String? = null,
    var submissionTime: String? = null
)
