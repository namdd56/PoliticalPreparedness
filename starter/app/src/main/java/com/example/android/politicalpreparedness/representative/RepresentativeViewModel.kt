package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

    //TODO: Establish live data for representatives and address
    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    init {
        _address.value = Address("", "", "", "", "")
        _representatives.value = arrayListOf()
    }

    //TODO: Create function to fetch representatives from API from a provided address
    fun fetchRepresentatives(address: Address) {
        viewModelScope.launch {
            try {
                if (address != null) {
                    _address.value = address
                    var addressString = _address.value!!.toFormattedString()
                    val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(addressString)
                    _representatives.value = offices.flatMap { office ->
                        office.getRepresentatives(officials)
                    }
                }
            } catch (e: Exception) {
                _representatives.value = arrayListOf()
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location


    //TODO: Create function to get address from individual fields
    fun updateAddress(addressLocal: Address) {
        _address.value = addressLocal
    }

    fun onReceivedErrorMessage() {
        _errorMessage.value = null
    }

}
