package com.example.velibapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.content.Intent
import android.Manifest
import java.text.Normalizer
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.view.Gravity
import android.widget.ImageButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.velibapp.data.api.RetrofitInstance
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var userLatitude = 48.8566
    private var userLongitude = 2.3522
    private var searchRadius: Float = 1000.0F
    private lateinit var mMap: GoogleMap
    private lateinit var drawerLayout: DrawerLayout
    private var stationsCache = listOf<com.example.velibapp.data.model.Station>()
    private var filterNearbyOnly = true

    private fun isNearbyStation(stationLat: Double, stationLon: Double): Boolean {
        val results = FloatArray(1)
        Location.distanceBetween(userLatitude, userLongitude, stationLat, stationLon, results)
        return results[0] <= searchRadius
    }

    private fun normalize(text: String): String {
        return Normalizer
            .normalize(text, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
    }

    private fun getUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                userLatitude = location.latitude
                userLongitude = location.longitude
                Log.d(
                    "LOCATION",
                    "$userLatitude / $userLongitude"
                )
                val userPosition = LatLng(userLatitude, userLongitude)
                mMap.isMyLocationEnabled = true
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        userPosition,
                        15f
                    )
                )

                mMap.clear()

                loadStations()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        drawerLayout = findViewById(R.id.drawerLayout)

        // Bouton hamburger
        findViewById<ImageButton>(R.id.btnMenu).setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }

        // Bouton favoris
        val etSearch =
            findViewById<EditText>(R.id.etSearch)

        val listSuggestions =
            findViewById<ListView>(
                R.id.listSuggestions
            )

        etSearch.addTextChangedListener(

            object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    val query =
                        s.toString().trim()

                    if (query.length < 2) {

                        listSuggestions.visibility =
                            android.view.View.GONE

                        return
                    }

                    val matches =
                        stationsCache
                            .filter {

                                it.name.contains(
                                    query,
                                    ignoreCase = true
                                )
                            }
                            .take(5)

                    val adapter =
                        ArrayAdapter(
                            this@MapsActivity,
                            android.R.layout.simple_list_item_1,
                            matches.map { it.name }
                        )

                    listSuggestions.adapter =
                        adapter

                    listSuggestions.visibility =
                        if (matches.isEmpty())
                            android.view.View.GONE
                        else
                            android.view.View.VISIBLE
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {}
            }
        )

        listSuggestions.setOnItemClickListener {

                _, _, position, _ ->

            val station =
                stationsCache
                    .filter {

                        it.name.contains(
                            etSearch.text.toString(),
                            ignoreCase = true
                        )
                    }
                    .take(5)[position]

            etSearch.setText(
                station.name
            )

            listSuggestions.visibility =
                android.view.View.GONE

            findViewById<ImageButton>(
                R.id.btnSearch
            ).performClick()
        }

        findViewById<ImageButton>(R.id.btnSearch)
            .setOnClickListener {

                val query = etSearch.text.toString().trim()

                val station = stationsCache.find {
                    normalize(it.name).contains(
                        normalize(query)
                    )
                }

                if (station != null) {

                    android.widget.Toast.makeText(
                        this,
                        station.name,
                        android.widget.Toast.LENGTH_LONG
                    ).show()

                    val position = LatLng(
                        station.lat,
                        station.lon
                    )

                    lifecycleScope.launch {

                        val statusResponse =
                            RetrofitInstance.api.getStationsStatus()

                        val status =
                            statusResponse.data.stations.find {
                                it.station_id == station.station_id
                            }

                        val bikes =
                            status?.num_bikes_available ?: 0

                        val docks =
                            status?.num_docks_available ?: 0

                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                position,
                                17f
                            ),
                            object : GoogleMap.CancelableCallback {

                                override fun onFinish() {

                                    StationDetailBottomSheet(
                                        station.station_id,
                                        station.name,
                                        bikes,
                                        docks
                                    ).show(
                                        supportFragmentManager,
                                        "station_detail"
                                    )
                                }

                                override fun onCancel() {}
                            }
                        )
                    }

                } else {

                    android.widget.Toast.makeText(
                        this,
                        "Station introuvable",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }

        findViewById<FloatingActionButton>(
            R.id.btnMyLocation
        ).setOnClickListener {

            filterNearbyOnly = true

            getUserLocation()
        }

        // Bouton nearby
        findViewById<FloatingActionButton>(R.id.btnNearby).setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_radius, null)
            dialog.setContentView(view)
            val seekBar = view.findViewById<android.widget.SeekBar>(R.id.seekRadius)
            val tvRadius = view.findViewById<TextView>(R.id.tvRadiusSheet)
            seekBar.progress = (searchRadius / 500).toInt()
            seekBar.max = 20
            tvRadius.text = "Rayon : ${searchRadius / 1000} km"
            seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                    val radiusKm = maxOf(0.5f, progress * 0.5f)
                    searchRadius = radiusKm * 1000
                    tvRadius.text = "Rayon : $radiusKm km"
                    filterNearbyOnly = true
                    mMap.clear()
                    loadStations()
                }
                override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
            })
            dialog.show()
        }

        // Items du menu drawer
        findViewById<LinearLayout>(R.id.menuAccount).setOnClickListener {
            drawerLayout.closeDrawers()
            startActivity(Intent(this, AccountActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuFavorites).setOnClickListener {
            drawerLayout.closeDrawers()
            startActivity(Intent(this, FavoriteActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuHowTo).setOnClickListener {
            drawerLayout.closeDrawers()
            startActivity(Intent(this, HowToActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuContact).setOnClickListener {
            drawerLayout.closeDrawers()
            startActivity(Intent(this, ContactActivity::class.java))
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        val paris = LatLng(48.8566, 2.3522)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 15f))
        mMap.setOnMarkerClickListener { marker ->
            val data = marker.tag as? List<*>
            if (data != null) {
                val bottomSheet = StationDetailBottomSheet(
                    data[0] as String, data[1] as String, data[2] as Int, data[3] as Int
                )
                bottomSheet.show(supportFragmentManager, "station_detail")
            }
            true
        }
        loadStations()
        getUserLocation()
    }

    private fun loadStations() {
        mMap.clear()
        lifecycleScope.launch {
            try {
                val stationsResponse = RetrofitInstance.api.getStations()
                val statusResponse = RetrofitInstance.api.getStationsStatus()
                val stations = stationsResponse.data.stations
                stationsCache = stations
                val statusList = statusResponse.data.stations
                stations.forEach { station ->
                    if (filterNearbyOnly) {
                        if (!isNearbyStation(station.lat, station.lon))
                            return@forEach
                    }
                    val status = statusList.find { it.station_id == station.station_id }
                    val bikes = status?.num_bikes_available ?: 0
                    val docks = status?.num_docks_available ?: 0
                    val position = LatLng(station.lat, station.lon)
                    val markerColor = when {
                        bikes == 0 -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
                        bikes <= 15 -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE
                        else -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
                    }
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(station.name)
                            .snippet("🚲 $bikes vélos")
                            .icon(
                                createCustomMarker(
                                    bikes,
                                    when {
                                        bikes == 0 -> android.graphics.Color.RED
                                        bikes <= 15 -> android.graphics.Color.rgb(255, 152, 0)
                                        else -> android.graphics.Color.rgb(76, 175, 80)
                                    }
                                )
                            )
                    )
                    marker?.tag = listOf(station.station_id, station.name, bikes, docks)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createCustomMarker(
        bikes: Int,
        color: Int
    ): com.google.android.gms.maps.model.BitmapDescriptor {

        val markerView = LayoutInflater.from(this)
            .inflate(R.layout.marker_station, null)

        val textView =
            markerView.findViewById<TextView>(
                R.id.tvMarkerValue
            )

        textView.text = bikes.toString()

        textView.background.setTint(color)

        markerView.measure(
            android.view.View.MeasureSpec.UNSPECIFIED,
            android.view.View.MeasureSpec.UNSPECIFIED
        )

        markerView.layout(
            0,
            0,
            markerView.measuredWidth,
            markerView.measuredHeight
        )

        val bitmap = Bitmap.createBitmap(
            markerView.measuredWidth,
            markerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        markerView.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
