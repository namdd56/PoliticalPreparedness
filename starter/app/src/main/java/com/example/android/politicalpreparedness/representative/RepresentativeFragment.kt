package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        //TODO: Add Constant for Location request
        private const val REQUEST_ACCESS_FINE_LOCATION = 101
    }

    //TODO: Declare ViewModel
    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this).get(RepresentativeViewModel::class.java)
    }
    lateinit var binding: FragmentRepresentativeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        //TODO: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        //TODO: Define and assign Representative adapter
        var representativeListAdapter = RepresentativeListAdapter()
        binding.recyclerRepresentatives.adapter = representativeListAdapter

        //TODO: Populate Representative adapter
        viewModel.representatives.observe(viewLifecycleOwner, Observer { representatives ->
            representativeListAdapter.submitList(representatives)
        })

        //TODO: Establish button listeners for field and location search
        binding.buttonSearchRepresentative.setOnClickListener {
            var address = Address(
                binding.addressLine1.text.toString(),
                binding.addressLine2.text.toString(),
                binding.city.text.toString(),
                binding.state.selectedItem.toString(),
                binding.zip.text.toString()
            )
            viewModel.fetchRepresentatives(address)

            hideKeyboard(it)
        }
        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    error,
                    Snackbar.LENGTH_LONG
                ).show()
                viewModel.onReceivedErrorMessage()
            }
        }
        return binding.root
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Precise location access granted.
            getLocation()
        } else {
            // No location access granted.
            Snackbar.make(
                requireView(),
                R.string.error_location_permission,
                Snackbar.LENGTH_SHORT
            ).show()
        }

    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray,
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        //TODO: Handle location permission result to get location on permission granted
//        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLocation()
//            } else {
//                Snackbar.make(
//                    requireView(),
//                    R.string.error_location_permission,
//                    Snackbar.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }

    @SuppressLint("MissingPermission")
    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            getLocation()
            true
        } else {
            //TODO: Request Location permissions
            locationPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION)
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //TODO: Get location from LocationServices
        val location = LocationServices.getFusedLocationProviderClient(requireContext())
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        location.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val address = geoCodeLocation(location)
                    viewModel.updateAddress(address)
                    Log.d("Location", "address: " + address)
                    viewModel.fetchRepresentatives(address)
                }
            }
            .addOnFailureListener { e ->
                Log.i("Location", "Error: " + e.localizedMessage)
            }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
            .map { address ->
                Address(
                    address?.thoroughfare.toString(),
                    address?.subThoroughfare.toString(),
                    address?.locality.toString(),
                    address?.adminArea.toString(),
                    address?.postalCode.toString()
                )
            }
            .first()
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}