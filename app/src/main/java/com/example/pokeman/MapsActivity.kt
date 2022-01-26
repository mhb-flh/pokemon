package com.example.pokeman

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.pokeman.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.lang.Exception
import com.example.pokeman.models.pokemon


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    var pokemonList=ArrayList<pokemon>()
    var playerPower=0.0
    val ACCESSlOCATION = 1
    var location: Location? = null
    var oldLocation:Location?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val Tehran = LatLng(35.7, 51.4)
        mMap.addMarker(
            MarkerOptions()
                .position(Tehran)
                .title("TEHRAN")
                .snippet("here is my location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Tehran, 14f))
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission
                    (
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESSlOCATION
                )
                return
            }
        }
        getUserLocation()
        LoadPokemon()
    }

    fun getUserLocation() {
        Toast.makeText(this, "get location", Toast.LENGTH_SHORT).show()
        var myLocation = myLocationListener()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, myLocation)
        var mThread=myThread()
        mThread.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            ACCESSlOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "cant get location", Toast.LENGTH_SHORT).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //get user Location
    inner class myLocationListener : LocationListener {
        constructor() {
            location = Location("start")
            location!!.latitude = 0.0
            location!!.longitude = 0.0
        }

        override fun onLocationChanged(p0: Location) {
            location = p0
        }
        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    }

    inner class myThread : Thread {

        constructor() : super() {
            oldLocation = Location("start")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0
        }
        override fun run() {
            while (true) {

                try {
                    if (oldLocation!!.distanceTo(location)==0f){
                        continue
                    }
                    oldLocation=location
                    runOnUiThread {
                        mMap.clear()

                            //show me
                        val Tehran = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(Tehran)
                                .title("my location")
                                .snippet("I am here now")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Tehran, 14f))

                        //show other pokemons
                        for (i in 0 until pokemonList.size){
                            var newPokemon=pokemonList[i]
                            if (!newPokemon.isCatch){
                                val pokemonLocation = LatLng(newPokemon.location!!.latitude, newPokemon.location!!.longitude)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(pokemonLocation)
                                        .title(newPokemon.name!!)
                                        .snippet(newPokemon.description!! + "power : "+newPokemon.Power!!)
                                        .icon(BitmapDescriptorFactory.fromResource(newPokemon.Image!!)))

                                if (location!!.distanceTo(newPokemon.location)<2){
                                    newPokemon.isCatch=true
                                    pokemonList[i]=newPokemon
                                    playerPower+= newPokemon.Power!!
                                    Toast.makeText(applicationContext,
                                        "you catch pokemon power your power is : $playerPower",Toast.LENGTH_SHORT).show()

                                }
                            }
                        }
                    }
                    sleep(3000)
                } catch (ex: Exception) {
                }
            }
        }
    }

    fun LoadPokemon () {
        pokemonList.add(pokemon(
            "bulbasaur","bulbasaur live in shiraz",R.drawable.bulbasaur,55.0,29.591768,52.583698))
        pokemonList.add(pokemon(
            "charmander","charmander live in bandar abbas",R.drawable.charmander,51.0,27.183708,56.277447))
        pokemonList.add(pokemon(
            "squirtle","squirtle live in shiraz",R.drawable.squirtle,65.0,29.107101,58.345772))
    }

}