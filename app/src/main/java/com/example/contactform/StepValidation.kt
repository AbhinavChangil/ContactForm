package com.example.contactform

interface StepValidation {
    fun isValid(): Boolean
    fun getErrorMessage(): String
}
