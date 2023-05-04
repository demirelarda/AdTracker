package com.mycompany.advioo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycompany.advioo.models.installer.Installer
import com.mycompany.advioo.repo.InstallerRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AvailableInstallersViewModel @Inject constructor(
    private val repository : InstallerRepositoryInterface
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

    private val _stateId = MutableLiveData<String>()
    val stateId : LiveData<String>
        get() = _stateId

    private val _installerList = MutableLiveData<List<Installer>>()
    val installerList: LiveData<List<Installer>>
        get() = _installerList




    fun getInstallersByState(stateId: String) {
        _loadingState.postValue(true)
        repository.getInstallersByState(stateId,
            onSuccess = { installersList ->
                _installerList.postValue(installersList)
                println("got the list")
                println("success size = "+installersList.size)
                _loadingState.postValue(false)
                _successState.postValue(true)
                _failState.postValue(false)

            },
            onFailure = {
                println("failed to get the list")
                _successState.postValue(false)
                _loadingState.postValue(false)
                _failState.postValue(true)
            })
    }


}