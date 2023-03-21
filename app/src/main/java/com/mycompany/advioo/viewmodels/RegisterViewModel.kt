package com.mycompany.advioo.viewmodels

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycompany.advioo.R

class RegisterViewModel : ViewModel(){

    private val _errorLiveData = MutableLiveData<Int>()
    val errorLiveData: LiveData<Int>
        get() = _errorLiveData


    val errorList : ArrayList<Int> = ArrayList()

    fun isInputDataValid(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): Boolean {
        var isValid = true

        errorList.clear()

        if (firstName.isEmpty()) {
            _errorLiveData.value = R.string.first_name_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if(lastName.isEmpty()){
            _errorLiveData.value = R.string.last_name_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if (email.isEmpty()) {
            _errorLiveData.value = R.string.email_empty_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorLiveData.value = R.string.email_invalid_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if (password.isEmpty()) {
            _errorLiveData.value = R.string.password_empty_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        } else if (password.length < 8) {
            _errorLiveData.value = R.string.password_length_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            _errorLiveData.value = R.string.password_confirm_empty_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if (!isValid) {
            return false
        }

        if (password != confirmPassword) {
            _errorLiveData.value = R.string.password_confirm_not_match
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        return isValid
    }
}