package com.mycompany.advioo.viewmodels

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.repo.CampaignRepositoryInterface
import com.mycompany.advioo.repo.UserRepositoryInterface
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
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


    private val _campaigns = MutableLiveData<List<Campaign>>()
    val campaigns: LiveData<List<Campaign>> = _campaigns


    fun getCampaigns(stateId: String){
        _loadingState.value = true
        repository.getCampaigns(stateId)
            .addOnSuccessListener {campaignList ->
                _campaigns.value = campaignList
                _loadingState.value = false

            }
            .addOnFailureListener {
                _loadingState.value = false
                _failState.value = true
            }
    }


}
