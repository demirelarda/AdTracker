package com.mycompany.advioo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val auth: FirebaseAuth
): ViewModel() {


    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _successState = MutableLiveData<Boolean>()
    val successState: LiveData<Boolean>
        get() = _successState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    private val _errorLiveData = MutableLiveData<Int>()
    val errorLiveData: LiveData<Int>
        get() = _errorLiveData

    val errorList : ArrayList<Int> = ArrayList()

    fun sendPasswordResetEmail(userEmail:String){
        _loadingState.postValue(true)
        auth.sendPasswordResetEmail(userEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loadingState.postValue(false)
                    _successState.postValue(true)
                } else {
                    _loadingState.postValue(false)
                    _failState.postValue(true)
                }
            }
    }

    fun isInputDataValid(email:String) : Boolean{
        var isValid = true
        if(email.isEmpty()){
            _errorLiveData.value = R.string.error_blank_email
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }
        return isValid
    }
}