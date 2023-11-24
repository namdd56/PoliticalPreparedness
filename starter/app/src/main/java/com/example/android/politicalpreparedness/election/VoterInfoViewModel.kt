package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val dataSource: ElectionDao) : ViewModel() {

    //TODO: Add live data to hold voter info
    private val _isCurrent = MutableLiveData<Boolean>()
    val isCurrent: LiveData<Boolean>
        get() = _isCurrent

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election>
        get() = _election

    private val _administrationBody = MutableLiveData<AdministrationBody>()
    val administrationBody: LiveData<AdministrationBody>
        get() = _administrationBody

    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    //TODO: Add var and methods to populate voter info
    fun populateVoterInfo(electionId: Int, division: Division) {
        viewModelScope.launch {
            try {
                val address = "${division.state}, ${division.country}"
                val voterInfoResponse = CivicsApi.retrofitService.getVoterInfo(address, electionId)
                _isCurrent.value = dataSource.getElection(electionId) != null
                _election.value = voterInfoResponse.election
                _administrationBody.value =
                    voterInfoResponse.state?.first()?.electionAdministrationBody
                _address.value = address
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }

        }
    }

    //TODO: Add var and methods to support loading URLs
    fun loadingURL(url: String) {
        _url.value = url
    }

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    fun saveOrRemoveElection() {
        viewModelScope.launch {
            _election.value?.let {
                if (_isCurrent.value == true) {
                    dataSource.deleteElection(it)
                    _isCurrent.value = false
                } else {
                    dataSource.insert(it)
                    _isCurrent.value = true
                }
            }
        }
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    fun onReceivedErrorMessage() {
        _errorMessage.value = null
    }

}