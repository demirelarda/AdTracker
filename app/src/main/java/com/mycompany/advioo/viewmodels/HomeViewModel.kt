package com.mycompany.advioo.viewmodels

import androidx.lifecycle.*
import com.mycompany.advioo.repo.CampaignRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository : CampaignRepositoryInterface
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


    val campaigns = repository.getCampaigns()
        .onStart {
            _loadingState.value = true
        }
        .onEach { campaigns ->
            _loadingState.value = false
            _successState.value = campaigns.isNotEmpty()
        }
        .catch { error ->
            _loadingState.value = false
            _failState.value = true
        }
        .asLiveData(viewModelScope.coroutineContext)

    init {
        _failState.value = false
    }


}