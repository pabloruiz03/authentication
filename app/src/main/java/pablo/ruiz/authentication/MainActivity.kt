package pablo.ruiz.authentication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
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
    val navController = rememberNavController()
    var isUserLoggedIn by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(auth) {
        isUserLoggedIn = auth.currentUser != null
    }

    if (isUserLoggedIn) {
        HomeScreen(navController = navController)
    } else {
        AuthScreen(onAuthSuccess = {
            isUserLoggedIn = true
            navController.navigate("home")
        })
    }
}
