package pablo.ruiz.authentication

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

@Composable
fun HomeScreen(navController: NavController) {
    var latitudeInput by remember { mutableStateOf("") }
    var longitudeInput by remember { mutableStateOf("") }
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    var markerIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }

    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.halloween)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input for latitude
        OutlinedTextField(
            value = latitudeInput,
            onValueChange = { latitudeInput = it },
            label = { Text("Latitude") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Input for longitude
        OutlinedTextField(
            value = longitudeInput,
            onValueChange = { longitudeInput = it },
            label = { Text("Longitude") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Button to place marker
        Button(
            onClick = {
                val lat = latitudeInput.toDoubleOrNull()
                val lng = longitudeInput.toDoubleOrNull()
                if (lat != null && lng != null && lat in -90.0..90.0 && lng in -180.0..180.0) {
                    markerPosition = LatLng(lat, lng)
                } else {
                    Toast.makeText(context, "Invalid coordinates.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Place Marker")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to change marker color
        Button(
            onClick = {
                markerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Change Marker Color")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons to control sound
        Button(
            onClick = {
                if (!isPlaying) {
                    mediaPlayer.start()
                    isPlaying = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Play Sound")
        }
        Button(
            onClick = {
                if (isPlaying) {
                    mediaPlayer.pause()
                    mediaPlayer.seekTo(0)
                    isPlaying = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Stop Sound")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to open map via deep link
        Button(
            onClick = {
                val lat = latitudeInput.toDoubleOrNull()
                val lng = longitudeInput.toDoubleOrNull()
                if (lat != null && lng != null && lat in -90.0..90.0 && lng in -180.0..180.0) {
                    val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Invalid coordinates.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Map")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Google Map
        MapViewComposable(markerPosition, markerIcon)
    }

    DisposableEffect(Unit) {
        onDispose {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
    }
}

@Composable
fun MapViewComposable(markerPosition: LatLng?, markerIcon: BitmapDescriptor?) {
    val initialPosition = LatLng(42.2406, -8.7207)
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
        markerPosition?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Custom Marker",
                icon = markerIcon
            )
            LaunchedEffect(it) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 12f))
            }
        }
    }
}
