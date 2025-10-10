package ca.unb.mobiledev.unb_mate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CampusMapActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campus_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // UNB Fredericton campus coordinates
        val unbCampus = LatLng(45.9454, -66.6417)

        googleMap.addMarker(
            MarkerOptions()
                .position(unbCampus)
                .title("University of New Brunswick - Fredericton")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(unbCampus, 15f))
    }
}
