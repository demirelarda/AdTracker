package com.mycompany.advioo.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycompany.advioo.R

class RegisterAddressDetailsViewModel : ViewModel() {

    private val _errorLiveData = MutableLiveData<Int>()
    val errorLiveData: LiveData<Int>
        get() = _errorLiveData


    val errorList : ArrayList<Int> = ArrayList()

    fun isInputDataValid(
        city: String,
        zipCode: String,
    ): Boolean {
        var isValid = true

        errorList.clear()

        if(city.isEmpty()){
            _errorLiveData.value = R.string.city_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }


        if (zipCode.isEmpty()) {
            _errorLiveData.value = R.string.zip_code_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if (!isValid) {
            return false
        }


        return isValid
    }

}