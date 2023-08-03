package com.app.tasty.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.tasty.databinding.LaunchPageBinding
import com.app.tasty.viewModel.LaunchPageViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.Locale

class LaunchPage : Fragment() {
    private var _binding: LaunchPageBinding? = null
    private val binding: LaunchPageBinding
        get() = _binding!!
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private val launchPageVM = LaunchPageViewModel()

    private val locationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        when {
            it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getLocation()
            }

            it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                getLocation()
            }

            else -> {
                Toast.makeText(
                    requireActivity(), "User Location Permission Denied!", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LaunchPage", "Anything")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = LaunchPageBinding.inflate(layoutInflater, container, false)
        activity?.let {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        launchPageVM.geoLocationLd.observe(
            viewLifecycleOwner
        ) {
            binding.apply {
                userLocation.text = it
            }
        }
        binding.menu.foodMenu.setOnClickListener {
            findNavController().navigate(
                directions = LaunchPageDirections.actionLaunchPageToFoodListPage()
            )
        }
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun hasUserGrantedPermissionForLocation(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    private fun checkPermissions() {
        if (hasUserGrantedPermissionForLocation()) {
            requestLocationPermission()
            Log.d("LaunchPage", "checkingPermissions")
        } else {
            getLocation()
            Log.d("LaunchPage", "getting Location")
        }
    }

    private fun requestLocationPermission() {
        locationPermission.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        Log.d("LaunchPage", "requestingPermissions")
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (!hasUserGrantedPermissionForLocation()) {
                if (isLocationEnabled()) {
                    mFusedLocationClient?.lastLocation?.addOnCompleteListener(
                        requireActivity()
                    ) { task ->
                        Log.d("LaunchPage", "in get location")
                        val location: Location? = task.result
                        Log.d("LaunchPage", "task.result = ${task.result}")
                        if (location != null) {
                            val geocoder =
                                context?.let { Geocoder(it, Locale.getDefault()) }
                            val list: List<Address> = geocoder?.getFromLocation(
                                location.latitude, location.longitude, 1
                            ) as List<Address>
                            launchPageVM.getAddress(address = list[0].getAddressLine(0))
                        }
                    }
                } else {
                    Toast.makeText(
                        requireActivity(), "Please turn on location", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}