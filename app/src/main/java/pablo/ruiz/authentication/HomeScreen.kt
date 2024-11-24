package pablo.ruiz.authentication

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth

// Main Home Screen composable function
@Composable
fun HomeScreen() {
    // Get an instance of Firebase Authentication to access the current user
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val context = LocalContext.current

    // Layout for the home screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome message showing the user's email
        Text(text = "Welcome, ${user?.email}")
        Spacer(modifier = Modifier.height(16.dp))

        // Display the Google Map
        MapViewComposable(context)

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Out button to log out the user
        Button(
            onClick = { auth.signOut() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Out")
        }
    }
}

// Composable function to display a Google Map
@Composable
fun MapViewComposable(context: Context) {
    // Remember the MapView to persist it across recompositions
    val mapView = remember { MapView(context) }
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }

    // Handle the lifecycle of the MapView
    DisposableEffect(mapView) {
        // Initialize the MapView
        mapView.onCreate(null)
        mapView.getMapAsync { map ->
            googleMap = map

            // Set up the initial map settings to show Vigo, Spain
            val vigoLocation = LatLng(42.2406, -8.7207) // Latitude and Longitude of Vigo, Spain
            googleMap?.apply {
                // Add a marker in Vigo, Spain
                addMarker(MarkerOptions().position(vigoLocation).title("Marker in Vigo, Spain"))
                // Move the camera to Vigo with a zoom level of 12
                moveCamera(CameraUpdateFactory.newLatLngZoom(vigoLocation, 12f))
            }
        }

        // Clean up the MapView when it is no longer needed
        onDispose {
            mapView.onDestroy()
        }
    }

    // Render the MapView inside the Compose layout
    AndroidView(
        factory = { mapView },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp), // Set a fixed height for the map
        update = { view ->
            // Resume the MapView when the composable is recomposed
            view.onResume()
        }
    )
}
