package com.mycompany.advioo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.models.ContactMessage
import com.mycompany.advioo.models.localuser.LocalDriver
import com.mycompany.advioo.repo.UserRepositoryInterface
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel @Inject constructor(
    private val userRepository: UserRepositoryInterface,
    private val localDriverRepository: LocalDriverRepositoryInterface,
    private val auth: FirebaseAuth
) : ViewModel(){

    private val _messageTitle = MutableLiveData<String>()
    val messageTitle: LiveData<String>
        get() = _messageTitle

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    private val _successState = MutableLiveData<Boolean>()
    val successState: LiveData<Boolean>
        get() = _successState

    private val _localUser = MutableLiveData<LocalDriver>()


    fun sendMessage(messageTitle: String, messageContent: String){
        _loadingState.value = true
        viewModelScope.launch {
            try {
                _localUser.value =  localDriverRepository.getDriver(auth.uid!!)
                val contactMessage = ContactMessage(
                    userId = auth.uid!!,
                    userEmail= auth.currentUser!!.email!!,
                    userFullName = _localUser.value!!.name + " " + _localUser.value!!.surname,
                    sentTime = Timestamp.now(),
                    title = messageTitle,
                    message = messageContent,
                    isSolved = false
                )
                userRepository.sendContactMessage(contactMessage)
                    .addOnSuccessListener {
                        _successState.value = true
                        _loadingState.value = false
                        _failState.value = false
                    }
                    .addOnFailureListener {
                        _successState.value = false
                        _failState.value = true
                        _loadingState.value = false
                        _errorMessage.value = it.localizedMessage
                    }
            }
            catch (e:Exception){
                _loadingState.value = false
                _failState.value = true
                _errorMessage.value = e.localizedMessage
            }

        }
    }

}