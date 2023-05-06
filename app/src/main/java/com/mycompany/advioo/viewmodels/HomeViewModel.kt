package com.mycompany.advioo.viewmodels

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.models.localuser.LocalDriver
import com.mycompany.advioo.repo.CampaignRepositoryInterface
import com.mycompany.advioo.repo.UserRepositoryInterface
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CampaignRepositoryInterface,
    private val localDriverRepository: LocalDriverRepositoryInterface,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    private val _saveDriverSuccess = MutableLiveData<Boolean>()
    val saveDriverSuccess: LiveData<Boolean>
        get() = _saveDriverSuccess

    private val _saveDriverFailure = MutableLiveData<Throwable?>()
    val saveDriverFailure: LiveData<Throwable?>
        get() = _saveDriverFailure

    private val _triggerCampaigns = MutableLiveData<Unit>()

    val campaigns: LiveData<List<Campaign>> = _triggerCampaigns.switchMap {
        liveData {
            _loadingState.value = true
            val stateId = localDriverRepository.getDriver(auth.uid!!)?.stateId
            if (stateId != null) {
                println("used local database for user")
                println("state id = $stateId")
                emitSource(repository.getCampaigns(stateId))
            } else {
                //_failState.value = true
                getUserFromFirestore()
            }
            _loadingState.value = false
        }
    }

    init {
        _triggerCampaigns.value = Unit
    }

    private fun getUserFromFirestore() {
        println("call from firestore to get user object ")
        _loadingState.value = true
        userRepository.getDriver(auth.uid!!)
            .addOnSuccessListener { driver ->
                val localDriver = LocalDriver(
                    id = driver!!.id,
                    name = driver.firstName,
                    surname = driver.lastName,
                    email = driver.email,
                    stateId = driver.userCity.stateId,
                    cityId = driver.userCity.cityId,
                    stateName = driver.userCity.stateName,
                    cityName = driver.userCity.cityName
                )
                saveDriver(localDriver)
            }
            .addOnFailureListener {
                println("error getting user data = "+it.localizedMessage)
                _loadingState.value = false
                _failState.value = true
            }
    }

    private fun saveDriver(localDriver: LocalDriver) {
        viewModelScope.launch {
            try {
                println("driver saved")
                localDriverRepository.saveDriver(localDriver)
                _saveDriverSuccess.value = true
                _saveDriverFailure.value = null
                _loadingState.value = false

                _triggerCampaigns.value = Unit

            } catch (error: Throwable) {
                println("error saving local data")
                _saveDriverSuccess.value = false
                _saveDriverFailure.value = error
                _loadingState.value = false
            }
        }
    }
}








