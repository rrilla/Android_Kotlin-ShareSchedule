package com.example.share_schedule.insertCalendar

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.share_schedule.R
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.share_schedule.BuildConfig
import com.example.share_schedule.base.BaseMapFragment
import com.example.share_schedule.databinding.FragmentMapBinding
import com.example.share_schedule.insertCalendar.util.showSnackbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar

class MapFragment: BaseMapFragment(), OnMapReadyCallback {

    override lateinit var mapView: MapView

    private lateinit var binding: FragmentMapBinding
    private val viewModel: MapViewModel by viewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    // 지도 관련, 옵션
    private var locationPermissionGranted = false
    private val DEFAULT_ZOOM = 15
    private val defaultLocation = LatLng(37.56, 126.97) // Seoul

    // 검색 옵션
    private var searchRankBy = "prominence"
    private var searchType = "restaurant"
    private var searchAuto = false

    private val autoCompleteLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        when (it.resultCode) {
            Activity.RESULT_OK -> {
                it.data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    map.clear()
                    search(place.latLng, place)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, DEFAULT_ZOOM.toFloat()))
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                it.data?.let {
                    val status = Autocomplete.getStatusFromIntent(it)
                    Log.e("MapFragment", status.statusMessage.toString())
                }
            }
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        when (it){
            true -> {
                Toast.makeText(context, "수락", Toast.LENGTH_SHORT).show()
                locationPermissionGranted = true
            }
            false -> {
                Toast.makeText(context, "거절", Toast.LENGTH_SHORT).show()
                locationPermissionGranted = false
                binding.root.showSnackbar(
                    R.string.location_access_required,
                    Snackbar.LENGTH_LONG,
                    R.string.ok
                ) {
                    val intent = Intent()
                    intent.data = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    startActivity(intent)
                }
            }
        }
        updateLocationUI()
        getDeviceLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Places.initialize(context, BuildConfig.PLACE_API_KEY)
        binding = FragmentMapBinding.inflate(layoutInflater)
        mapView = binding.map
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        getLocationPermission()

        return binding.root
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.autoButton.setOnClickListener {
            binding.alertLayout.isVisible = false
            //  반환할 데이터 유형
            val fields = listOf(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(requireContext())
            autoCompleteLaunch.launch(intent)
        }

        initView()
        observe()
    }

    private fun initView() {
        initSpinner()
        binding.switchAutoSearch.setOnCheckedChangeListener { _, isChecked ->
            searchAuto = isChecked
            map.clear()
        }
        binding.deleteButton.setOnClickListener {
            map.clear()
        }
        binding.alertButtonOk.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
            parentFragmentManager.popBackStack()
        }
        binding.alertButtonCancel.setOnClickListener {
            shareViewModel.setSelectLocation(mapOf())
            binding.alertLayout.visibility = View.GONE
        }
    }

    private fun initSpinner() {
        val spinnerRankBy: Spinner = binding.spinnerRankBy
        val spinnerType: Spinner = binding.spinnerType

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.search_rankBy,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRankBy.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.search_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerType.adapter = adapter
        }

        spinnerRankBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        searchRankBy = "prominence"
                        map.clear()
                    }
                    1 -> {
                        searchRankBy = "distance"
                        map.clear()
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        searchType = "restaurant"
                        map.clear()
                    }
                    1 -> {
                        searchType = "cafe"
                        map.clear()
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun observe() {
        viewModel.placeLiveData.observe(this){
            //마커표시
            if(searchAuto){
            } else {
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        with (googleMap) {
            map = this
            //  카메라 이동 멈출시 동작
            setOnCameraIdleListener {
                binding.centerIcon.isVisible = true
                if(searchAuto){
                    //  이동할 때 마다 검색 후 마커표시
                    search(map.cameraPosition.target, null)
                }
            }
            setOnCameraMoveStartedListener {
                binding.centerIcon.isVisible = false
            }
            setOnMarkerClickListener {
                binding.alertTitle.text = "'${it.title}' 으로 \n위치를 추가 하시겠습니까?"
                binding.alertLayout.isVisible = true
                shareViewModel.setSelectLocation(mapOf("title" to it.title!!, "address" to it.snippet!!))
                false
            }
            setOnMapClickListener { binding.alertLayout.isVisible = false }
            updateLocationUI()
            getDeviceLocation()
        }
    }

    private fun search(location: LatLng, place: Place?) {
        if(!searchAuto) {
            map.addMarker(
                MarkerOptions().position(location).title(place?.name).snippet(place?.address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        }
        if(searchRankBy == "prominence"){
            viewModel.searchByProminence(location, searchType)
        }else{
            viewModel.searchByDistance(location, searchType)
        }
    }

    // 사용자의 위치 정보 권한에 따라 지도 위치 컨트롤 설정.
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (locationPermissionGranted) {
            map.isMyLocationEnabled = true
            map.uiSettings.apply {
                isMyLocationButtonEnabled = true
                isZoomControlsEnabled = true
            }
        } else {
            map.isMyLocationEnabled = false
            map.uiSettings.apply {
                isMyLocationButtonEnabled = false
                isZoomControlsEnabled = false
            }
        }
    }

    //  사용자 위치 가져온 후 지도에 지정
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        if (locationPermissionGranted) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    val lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                        search(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude), null)
                    }
                } else {
                    map.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                    search(defaultLocation, null)
                }
            }
        }else{
            map.moveCamera(CameraUpdateFactory
                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
            search(defaultLocation, null)
        }
    }
}