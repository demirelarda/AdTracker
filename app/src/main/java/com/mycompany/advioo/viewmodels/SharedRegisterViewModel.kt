package com.mycompany.advioo.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.mycompany.advioo.models.localuser.LocalDriver
import com.mycompany.advioo.models.user.Driver
import com.mycompany.advioo.models.user.UserCity
import com.mycompany.advioo.repo.UserRepositoryInterface
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedRegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val driverData:Driver,
    private val repository: LocalDriverRepositoryInterface,
    private val userRepository: UserRepositoryInterface
): ViewModel(){


    private val _driver = MutableLiveData<Driver>(driverData)
    val driver: LiveData<Driver>
        get() = _driver

    private val _localDriver = MutableLiveData<LocalDriver>()
    val localDriver: LiveData<LocalDriver>
        get() = _localDriver

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _successState = MutableLiveData<Boolean>()
    val successState: LiveData<Boolean>
        get() = _successState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _editMode = MutableLiveData<Boolean>()
    val editMode: LiveData<Boolean>
        get() = _editMode




    fun setEditMode(editModeOption: Boolean){
        _editMode.value = editModeOption
    }


    fun setFirstName(firstName: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(firstName = firstName)
            _driver.value = updatedUser
        }
    }

    fun setLastName(lastName: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(lastName = lastName)
            _driver.value = updatedUser
        }
    }

    fun setEmail(email: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(email = email)
            _driver.value = updatedUser
        }
    }

    fun setPhoneNumber(phoneNumber: String){
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(driverPhoneNumber = phoneNumber)
            _driver.value = updatedUser
        }
    }

    fun setCity(city: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(city = city)
            _driver.value = updatedUser
        }
    }

    fun setCityObject(userCity:UserCity){
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(userCity = userCity)
            _driver.value = updatedUser
        }
    }




    fun setZipCode(zipCode: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(zipCode = zipCode)
            _driver.value = updatedUser
        }
    }

    fun setCarBrand(carBrand: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(carBrand = carBrand)
            _driver.value = updatedUser
        }
    }

    fun setCarModel(carModel: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(carModel = carModel)
            _driver.value = updatedUser
        }
    }

    fun setCarYear(carYear: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(carYear = carYear)
            _driver.value = updatedUser
        }
    }

    fun setCarCondition(carCondition: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(carCondition=carCondition)
            _driver.value = updatedUser
        }
    }

    fun setAvgKmDriven(avgKmDriven: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(avgKmDriven = avgKmDriven)
            _driver.value = updatedUser
        }
    }





    fun setAllowContact(allowContact: Boolean) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(allowedContact = allowContact)
            _driver.value = updatedUser
        }
    }

    fun setRideShare(rideShare:Boolean) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(rideShareDriver = rideShare)
            _driver.value = updatedUser
        }
    }

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password


    fun setPassword(password: String) {
        _password.postValue(password)
    }

    fun getLocalDriver() {
        viewModelScope.launch {
            try {
                val driver = repository.getDriver(auth.uid!!)
                if (driver != null) {
                    _localDriver.value = driver!!
                } else {
                    //TODO: get driver from firestore
                }
            } catch (e: Exception) {
                //TODO: handle error, get driver from firestore
            }
        }
    }


    fun updateUserPersonalDetails(updatedEmail: String, updatedName: String, updatedSurname: String, updatedPhoneNumber: String) {
        _loadingState.value = true
        val currentUserObject = _localDriver.value
        currentUserObject?.let {
            val updatedUser = it.copy(
                email = updatedEmail,
                name = updatedName,
                surname = updatedSurname,
                phoneNumber = updatedPhoneNumber
            )
            _localDriver.value = updatedUser
            val updateMap = mutableMapOf<String, Any>()
            updateMap["email"] = _localDriver.value!!.email
            updateMap["name"] = _localDriver.value!!.name
            updateMap["lastName"] = _localDriver.value!!.surname
            updateMap["driverPhoneNumber"] = _localDriver.value!!.phoneNumber
            updateDriver(email = updatedEmail, firstName = updatedName, lastName = updatedSurname, updateMap=updateMap)

        }
    }

    private fun updateDriver(email:String,firstName: String,lastName: String, updateMap: Map<String,Any>){
        userRepository.updateDriverData(auth.uid!!,updateMap).addOnSuccessListener {
            updateUserEmail(email,firstName,lastName)
        }
            .addOnFailureListener {
                _failState.value = true
                _loadingState.value = false
            }
    }

    private fun updateUserEmail(email:String,firstName: String,lastName: String){
        auth.currentUser!!.updateEmail(email)
            .addOnSuccessListener {
                updateUserName(firstName,lastName)
            }
            .addOnFailureListener {
                _failState.value = true
                _errorMessage.value = it.localizedMessage
                _loadingState.value = false
            }

    }

    private fun updateUserName(firstName:String,lastName: String){

        val fullName = "$firstName $lastName"
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .build()

        auth.currentUser?.updateProfile(profileUpdates)
            ?.addOnSuccessListener {
                updateLocalDriver()
            }
            ?.addOnFailureListener {
                _failState.value = true
                _errorMessage.value = it.localizedMessage
                _loadingState.value = false
            }

    }

    private fun updateLocalDriver(){
        viewModelScope.launch {
            try {
                println("local saving")
                repository.saveDriver(_localDriver.value!!)
                _successState.value = true
                _loadingState.value = false
            }
            catch (e: Exception){
                _failState.value = true
                _errorMessage.value = e.localizedMessage
                _loadingState.value = false
            }

        }
    }


    fun updateWorkDetails(carBrand: String, carModel: String, carYear: String, carCondition: String,
                          avgKmDriven: String, rideShare: Boolean, allowContact: Boolean) {
        _loadingState.value = true
        val currentUserObject = _localDriver.value
        currentUserObject?.let {
            val updatedUser = it.copy(
                carBrand = carBrand,
                carModel = carModel,
                carYear = carYear,
                carCondition = carCondition,
                avgKmDriven = avgKmDriven,
                rideShareDriver = rideShare,
                allowedContact = allowContact
            )
            _localDriver.value = updatedUser
        }

        val updateMap = mutableMapOf<String, Any>()
        updateMap["carBrand"] = _localDriver.value!!.carBrand
        updateMap["carModel"] = _localDriver.value!!.name
        updateMap["carYear"] = _localDriver.value!!.carYear
        updateMap["carCondition"] = _localDriver.value!!.carCondition
        updateMap["avgKmDriven"] = _localDriver.value!!.avgKmDriven
        updateMap["rideShareDriver"] = _localDriver.value!!.rideShareDriver
        updateMap["allowedContact"] = _localDriver.value!!.allowedContact
        updateDriverWorkData(updateMap)

    }

     fun updateDriverAddressData(city:String, zipCode:String){
        _loadingState.value = true
        val currentUserObject = _localDriver.value
        currentUserObject?.let {
            val updatedCity = _driver.value!!.userCity
            val updatedUser = it.copy(
                city = city,
                zipCode = zipCode,
                cityId = updatedCity.cityId,
                stateId = updatedCity.stateId,
                cityName = updatedCity.cityName,
                stateName = updatedCity.stateName
            )
            _localDriver.value = updatedUser
        }

        val updateMap = mutableMapOf<String, Any>()
        updateMap["city"] = _localDriver.value!!.city
        updateMap["cityId"] = _localDriver.value!!.cityId
        updateMap["stateId"] = _localDriver.value!!.stateId
        updateMap["cityName"] = _localDriver.value!!.cityName
        updateMap["stateName"] = _localDriver.value!!.stateName
        updateMap["userCity"] = _driver.value!!.userCity
        updateDriverWorkData(updateMap)

    }


    private fun updateDriverWorkData(updateFields: Map<String,Any>){
        userRepository.updateDriverData(auth.uid!!,updateFields)
            .addOnSuccessListener {
                updateLocalDriver()
            }
            .addOnFailureListener {
                _loadingState.value = false
                _errorMessage.value = it.localizedMessage
                _failState.value = true
            }
    }





}