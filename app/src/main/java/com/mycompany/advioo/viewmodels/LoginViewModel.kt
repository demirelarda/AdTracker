package com.mycompany.advioo.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.util.HaversineCalculateDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _successState = MutableLiveData<Boolean>()
    val successState: LiveData<Boolean>
        get() = _successState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    fun loginUser(email: String,password: String){
        _loadingState.postValue(true)
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task->
            if(task.isSuccessful){
                _loadingState.postValue(false)
                _successState.postValue(true)
            }
            else{
                _loadingState.postValue(false)
                _failState.postValue(true)
            }
        }
    }


    fun isInputDataValid(
        email: String,
        password: String,
    ):Boolean{
        var isValid = true
        if(email.isEmpty() || password.isEmpty()){
            isValid = false
        }
        if (!isValid) {
            return false
        }
        return isValid

    }


}