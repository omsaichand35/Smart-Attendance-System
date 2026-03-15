package com.example.smartattendance.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.smartattendance.api.RetrofitClient
import com.example.smartattendance.databinding.ActivityMarkAttendanceBinding
import com.example.smartattendance.models.CreateSessionRequest
import com.example.smartattendance.models.MarkAttendanceRequest
import com.example.smartattendance.models.Session
import com.example.smartattendance.utils.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MarkAttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarkAttendanceBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sessionManager: SessionManager
    private var currentLocation: Location? = null
    private val PERMISSION_REQUEST_CODE = 100
    private var courseId: Long = -1
    private var currentSession: Session? = null
    private var isTeacher: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        courseId = intent.getLongExtra("courseId", -1)
        isTeacher = intent.getBooleanExtra("isTeacher", false) || sessionManager.getRole().equals("TEACHER", ignoreCase = true)

        binding.apply {
            backButton.setOnClickListener { finish() }
            markAttendanceButton.text = if (isTeacher) "Set Location" else "Mark Attendance"
            markAttendanceButton.setOnClickListener {
                if (isTeacher) {
                    setSessionLocation()
                } else {
                    markAttendance()
                }
            }
            cancelButton.setOnClickListener { finish() }
        }

        loadSessions()
        requestLocationPermission()
        getLastLocation()
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    updateLocationUI(location)
                } else {
                    Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateLocationUI(location: Location) {
        binding.apply {
            if (isTeacher) {
                distanceText.text = String.format("%.5f, %.5f", location.latitude, location.longitude)
                locationStatus.text = "Current classroom location ready"
                locationStatus.setTextColor(ContextCompat.getColor(this@MarkAttendanceActivity, android.R.color.holo_green_dark))
                markAttendanceButton.isEnabled = true
            } else {
                val session = currentSession
                if (session == null) {
                    distanceText.text = "Session unavailable"
                    locationStatus.text = "No active session location set by teacher"
                    locationStatus.setTextColor(ContextCompat.getColor(this@MarkAttendanceActivity, android.R.color.holo_red_dark))
                    markAttendanceButton.isEnabled = false
                    return@apply
                }

                val distance = calculateDistance(location, session)
                distanceText.text = String.format("%.1f meters", distance)
                locationStatus.text = if (distance <= session.radius) "Within geofence" else "Outside geofence"
                locationStatus.setTextColor(
                    if (distance <= session.radius) ContextCompat.getColor(this@MarkAttendanceActivity, android.R.color.holo_green_dark)
                    else ContextCompat.getColor(this@MarkAttendanceActivity, android.R.color.holo_red_dark)
                )
                markAttendanceButton.isEnabled = distance <= session.radius
            }
        }
    }

    private fun calculateDistance(userLocation: Location, session: Session): Float {
        val sessionLocation = Location("session")
        sessionLocation.latitude = session.latitude
        sessionLocation.longitude = session.longitude

        return userLocation.distanceTo(sessionLocation)
    }

    private fun loadSessions() {
        if (courseId <= 0 || isTeacher) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getApiService(this@MarkAttendanceActivity).listSessions(courseId)
                if (response.success && !response.data.isNullOrEmpty()) {
                    currentSession = response.data.first()
                    runOnUiThread {
                        binding.sessionTime.text = "${currentSession?.startTime} to ${currentSession?.endTime}"
                        currentLocation?.let { updateLocationUI(it) }
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun markAttendance() {
        val session = currentSession
        if (currentLocation == null) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            return
        }
        if (session == null) {
            Toast.makeText(this, "No session location available", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = MarkAttendanceRequest(
                    sessionId = session.id,
                    latitude = currentLocation!!.latitude,
                    longitude = currentLocation!!.longitude
                )

                val response = RetrofitClient.getApiService(this@MarkAttendanceActivity).markAttendance(request)

                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    if (response.success) {
                        Toast.makeText(this@MarkAttendanceActivity, "Attendance marked successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@MarkAttendanceActivity, response.message, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MarkAttendanceActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setSessionLocation() {
        if (currentLocation == null) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val startTime = LocalDateTime.now()
                val endTime = startTime.plusHours(1)
                val request = CreateSessionRequest(
                    courseId = courseId,
                    latitude = currentLocation!!.latitude,
                    longitude = currentLocation!!.longitude,
                    radius = 100.0,
                    startTime = startTime.toString(),
                    endTime = endTime.toString()
                )
                val response = RetrofitClient.getApiService(this@MarkAttendanceActivity).startSession(request)

                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    if (response.success && response.data != null) {
                        currentSession = response.data
                        binding.sessionTime.text = "${response.data.startTime} to ${response.data.endTime}"
                        Toast.makeText(this@MarkAttendanceActivity, "Location set for this class", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@MarkAttendanceActivity, response.message, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MarkAttendanceActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }
}
