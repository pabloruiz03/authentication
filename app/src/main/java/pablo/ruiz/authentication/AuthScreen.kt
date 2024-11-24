package pablo.ruiz.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

// Composable function for the Authentication screen
// Handles user login and registration (sign-in/sign-up)
@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    // State variables for user input and UI state
    var email by remember { mutableStateOf("") } // Email input field state
    var password by remember { mutableStateOf("") } // Password input field state
    var showPassword by remember { mutableStateOf(false) } // Toggles password visibility
    var errorMessage by remember { mutableStateOf<String?>(null) } // Error message state
    val auth = FirebaseAuth.getInstance() // Firebase authentication instance

    // Layout for the authentication screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email input field
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp)) // Adds space between components

        // Password input field with visibility toggle
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Hide" else "Show") // Toggles text between "Hide" and "Show"
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp)) // Adds space between components

        // Button for signing in the user
        Button(
            onClick = {
                signIn(auth, email, password) { success, error ->
                    if (success) {
                        onAuthSuccess() // Navigate to the next screen if successful
                    } else {
                        errorMessage = error // Display error message
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In") // Label for the sign-in button
        }
        Spacer(modifier = Modifier.height(8.dp)) // Adds space between components

        // Button for registering a new user
        Button(
            onClick = {
                signUp(auth, email, password) { success, error ->
                    if (success) {
                        onAuthSuccess() // Navigate to the next screen if successful
                    } else {
                        errorMessage = error // Display error message
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up") // Label for the sign-up button
        }

        // Displays error messages if present
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error) // Error text in red
        }
    }
}

// Function to handle user sign-in
private fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onResult: (Boolean, String?) -> Unit
) {
    // Validate inputs
    if (email.isBlank() || password.isBlank()) {
        onResult(false, "Email and password cannot be empty")
        return
    }

    // Attempt to sign in with Firebase Authentication
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, null) // Successful sign-in
            } else {
                // Handle errors based on exception type
                val errorMessage = when (task.exception) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid password."
                    is FirebaseAuthInvalidUserException -> "No account found with this email."
                    is FirebaseNetworkException -> "Network error. Please check your connection."
                    else -> "Authentication failed. Please try again."
                }
                onResult(false, errorMessage) // Return error message
            }
        }
}

// Function to handle user sign-up
private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onResult: (Boolean, String?) -> Unit
) {
    // Validate inputs
    if (email.isBlank() || password.isBlank()) {
        onResult(false, "Email and password cannot be empty")
        return
    }

    // Ensure password meets length requirements
    if (password.length < 6) {
        onResult(false, "Password must be at least 6 characters long")
        return
    }

    // Attempt to create a new user with Firebase Authentication
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, null) // Successful sign-up
            } else {
                onResult(false, task.exception?.message) // Return error message
            }
        }
}
