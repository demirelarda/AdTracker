package com.mycompany.advioo.viewmodels


import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.mycompany.advioo.R
import com.mycompany.advioo.models.auth.RegisterResult
import com.mycompany.advioo.models.user.Driver
import com.mycompany.advioo.repo.UserRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterUserWorkDetailsViewModel @Inject constructor(
    private val repository : UserRepositoryInterface,
    private val auth: FirebaseAuth,
)  : ViewModel() {


    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _successState = MutableLiveData<Boolean>()
    val successState: LiveData<Boolean>
        get() = _successState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState



    fun uploadUserData(driver: Driver) {
        repository.uploadUserData(driver)
            .addOnSuccessListener {
                _loadingState.postValue(false)
                _successState.postValue(true)
            }
            .addOnFailureListener {
                _loadingState.postValue(false)
                _failState.postValue(false)
            }
    }




    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun registerUser(email: String, password: String, driver: Driver) {
        _loadingState.postValue(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    driver.id = auth.uid.toString()
                    val newDisplayName = driver.firstName+" "+driver.lastName
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(newDisplayName)
                        .build()
                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                uploadUserData(driver)
                            }
                            else{
                                _loadingState.postValue(false)
                                _failState.postValue(true)
                            }
                        }

                } else {
                    _loadingState.postValue(false)
                    _failState.postValue(true)
                }
            }
    }



    private val _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess: LiveData<Boolean>
        get() = _uploadSuccess

    fun saveUserData(driver: Driver) {
        repository.uploadUserData(driver)
            .addOnSuccessListener {
                _uploadSuccess.postValue(true)
            }
            .addOnFailureListener {
                _uploadSuccess.postValue(false)
            }
    }










    private val _errorLiveData = MutableLiveData<Int>()
    val errorLiveData: LiveData<Int>
        get() = _errorLiveData


    val errorList : ArrayList<Int> = ArrayList()

    fun isInputDataValid(
        carBrand: String,
        carModel: String,
        carYear: String,
        carCondition: String,
        licensePlate: String,
        avgKmDriven: String,
        workCity: String,
        termsConditions: CheckBox,
    ): Boolean {
        var isValid = true

        errorList.clear()

        if (carBrand.isEmpty()) {
            _errorLiveData.value = R.string.car_brand_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if(carModel.isEmpty()){
            _errorLiveData.value = R.string.car_model_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if (carYear.isEmpty()) {
            _errorLiveData.value = R.string.car_year_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if (carCondition.isEmpty()) {
            _errorLiveData.value = R.string.car_condition_error
            errorList.add(_errorLiveData.value!!)
            isValid = false

        }

        if (licensePlate.isEmpty()) {
            _errorLiveData.value = R.string.license_plate_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if (avgKmDriven.isEmpty()) {
            _errorLiveData.value = R.string.avg_km_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if (workCity.isEmpty()) {
            _errorLiveData.value = R.string.working_city_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if(!(termsConditions.isChecked)){
            _errorLiveData.value = R.string.terms_conditions_error
            errorList.add(_errorLiveData.value!!)
            isValid = false
        }

        if (!isValid) {
            return false
        }

        return isValid
    }

}