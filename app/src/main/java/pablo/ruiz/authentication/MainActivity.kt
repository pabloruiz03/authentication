package pablo.ruiz.authentication
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import pablo.ruiz.authentication.AuthScreen
import pablo.ruiz.authentication.HomeScreen
import pablo.ruiz.authentication.ui.theme.AuthenticationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthenticationTheme {
                AuthenticationApp()
            }
        }
    }
}

@Composable
fun AuthenticationApp() {
    var isUserLoggedIn by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    if (auth.currentUser != null) {
        isUserLoggedIn = true
    }

    if (isUserLoggedIn) {
        // Navigate to Home Screen
        HomeScreen()
    } else {
        // Show Authentication Screen
        AuthScreen(onAuthSuccess = { isUserLoggedIn = true })
    }
}
