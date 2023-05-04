package com.mycompany.advioo.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.models.user.Driver
import com.mycompany.advioo.models.user.UserCity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedRegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val driverData:Driver,
): ViewModel(){


    private val _driver = MutableLiveData<Driver>(driverData)
    val driver: LiveData<Driver>
        get() = _driver

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

    fun setAddressFullName(addressFullName: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(addressFullName = addressFullName)
            _driver.value = updatedUser
        }
    }

    fun setAddressRow1(addressRow1: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(addressRow1 = addressRow1)
            _driver.value = updatedUser
        }
    }

    fun setAddressRow2(addressRow2: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(addressRow2 = addressRow2)
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

    fun setLicensePlate(licensePlate: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(licensePlate = licensePlate)
            _driver.value = updatedUser
        }
    }

    fun setWorkCity(workCity: String) {
        val currentUserObject = _driver.value
        currentUserObject?.let {
            val updatedUser = it.copy(workCity = workCity)
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


}