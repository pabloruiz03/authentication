package pablo.ruiz.authentication

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.*

@Composable
fun HomeScreen() {
    // States to hold user input
    var latitudeInput by remember { mutableStateOf("") }
    var longitudeInput by remember { mutableStateOf("") }
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome text
        Text(text = "Welcome")
        Spacer(modifier = Modifier.height(16.dp))

        // Input field for latitude
        OutlinedTextField(
            value = latitudeInput,
            onValueChange = { latitudeInput = it },
            label = { Text("Latitude") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Input field for longitude
        OutlinedTextField(
            value = longitudeInput,
            onValueChange = { longitudeInput = it },
            label = { Text("Longitude") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Button to place the marker
        Button(
            onClick = {
                val lat = latitudeInput.toDoubleOrNull()
                val lng = longitudeInput.toDoubleOrNull()
                // Validate coordinates
                if (lat != null && lng != null && lat in -90.0..90.0 && lng in -180.0..180.0) {
                    markerPosition = LatLng(lat, lng)
                } else {
                    // Show a message if the input is invalid
                    Toast.makeText(context, "Please enter valid coordinates.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Place Marker")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Display the map with the marker
        MapViewComposable(markerPosition)
    }
}

@Composable
fun MapViewComposable(markerPosition: LatLng?) {
    val initialPosition = LatLng(42.2406, -8.7207) // Vigo, Spain
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 12f)
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = true)
    ) {
        // Add the marker to the map if a position is provided
        markerPosition?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Custom Marker",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE) // Blue marker
            )
            // Animate the camera to focus on the marker
            LaunchedEffect(it) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 12f))
            }
        }
    }
}
