package com.mycompany.advioo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyCampaignsViewModel @Inject constructor(
    private val localDriverRepository: LocalDriverRepositoryInterface,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    private val _campaignApplication = MutableLiveData<CampaignApplication>()
    val campaignApplication : LiveData<CampaignApplication>
        get() = _campaignApplication

    val campaignApplicationIsEmpty = MutableLiveData<Boolean>()




}