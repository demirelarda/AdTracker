package com.mycompany.advioo.viewmodels


import android.service.autofill.UserData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.R
import com.mycompany.advioo.models.auth.RegisterResult
import com.mycompany.advioo.models.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedRegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userData:User,
): ViewModel(){


    private val _user = MutableLiveData<User>(userData)
    val user: LiveData<User>
        get() = _user

    fun setFirstName(firstName: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(firstName = firstName)
            _user.value = updatedUser
        }
    }

    fun setLastName(lastName: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(lastName = lastName)
            _user.value = updatedUser
        }
    }

    fun setEmail(email: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(email = email)
            _user.value = updatedUser
        }
    }

    fun setCity(city: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(city = city)
            _user.value = updatedUser
        }
    }

    fun setAddressFullName(addressFullName: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(addressFullName = addressFullName)
            _user.value = updatedUser
        }
    }

    fun setAddressRow1(addressRow1: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(addressRow1 = addressRow1)
            _user.value = updatedUser
        }
    }

    fun setAddressRow2(addressRow2: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(addressRow2 = addressRow2)
            _user.value = updatedUser
        }
    }

    fun setZipCode(zipCode: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(zipCode = zipCode)
            _user.value = updatedUser
        }
    }

    fun setCarBrand(carBrand: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(carBrand = carBrand)
            _user.value = updatedUser
        }
    }

    fun setCarModel(carModel: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(carModel = carModel)
            _user.value = updatedUser
        }
    }

    fun setCarYear(carYear: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(carYear = carYear)
            _user.value = updatedUser
        }
    }

    fun setCarCondition(carCondition: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(carCondition=carCondition)
            _user.value = updatedUser
        }
    }

    fun setAvgKmDriven(avgKmDriven: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(avgKmDriven = avgKmDriven)
            _user.value = updatedUser
        }
    }

    fun setLicensePlate(licensePlate: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(licensePlate = licensePlate)
            _user.value = updatedUser
        }
    }

    fun setWorkCity(workCity: String) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(workCity = workCity)
            _user.value = updatedUser
        }
    }

    fun setAllowContact(allowContact: Boolean) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(allowedContact = allowContact)
            _user.value = updatedUser
        }
    }

    fun setRideShare(rideShare:Boolean) {
        val currentUserObject = _user.value
        currentUserObject?.let {
            val updatedUser = it.copy(rideShareDriver = rideShare)
            _user.value = updatedUser
        }
    }


    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password


    fun setPassword(password: String) {
        _password.postValue(password)
    }


}