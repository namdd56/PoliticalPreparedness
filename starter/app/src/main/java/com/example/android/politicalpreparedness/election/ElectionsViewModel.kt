package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(
    private val civicsApiService: CivicsApiService,
    private val electionDao: ElectionDao,
) : ViewModel() {

    //TODO: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    //TODO: Create live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election?>>()
    val savedElections: LiveData<List<Election?>>
        get() = _savedElections

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    init {
        getUpcomingElectionsFromAPI()
        getSavedElectionsFromDB()
    }

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    private fun getUpcomingElectionsFromAPI() {
        viewModelScope.launch {
            try {
                val response = civicsApiService.getElections()
                _upcomingElections.value = response.elections
                electionDao.insert(response.elections[0])
            } catch (e: Exception) {
                _upcomingElections.value = ArrayList()
                _errorMessage.value = "Error: ${e.message}"
                Log.e("ElectionsViewModel::getUpcomingElectionsFromAPI", "Error: " + _errorMessage.value)
            }
        }
    }

    private fun getSavedElectionsFromDB() {
        viewModelScope.launch {
            try {
                _savedElections.value = electionDao.getAllElections()
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e("ElectionsViewModel::getSavedElectionsFromDB", "Error: " + _errorMessage.value)
            }
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info
    private val _navigateToVoterInfo = MutableLiveData<Election?>()
    val navigateToVoterInfo: LiveData<Election?>
        get() = _navigateToVoterInfo

    fun displayVoterInfo(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun displayVoterInfoComplete() {
        _navigateToVoterInfo.value = null
    }

    fun onReceivedErrorMessage() {
        _errorMessage.value = null
    }
}