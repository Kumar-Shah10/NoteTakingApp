package com.example.notetaking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notetaking.ViewModel.UserViewModel
import com.example.notetaking.model.UserModel
import com.example.notetaking.repo.UserRepoImpl
import com.example.notetaking.ui.theme.DarkBackground
import com.example.notetaking.ui.theme.ErrorBg
import com.example.notetaking.ui.theme.ErrorText
import com.example.notetaking.ui.theme.Gold
import com.example.notetaking.ui.theme.NoteTakingTheme
import com.example.notetaking.ui.theme.TextFaint
import com.example.notetaking.ui.theme.TextMuted
import com.example.notetaking.ui.theme.TextPlaceholder
import com.example.notetaking.ui.theme.TextPrimary
import com.example.notetaking.ui.theme.TextTagline
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteTakingTheme {
                RegisterBody()
            }
        }
    }
}

@Composable
fun RegisterBody() {
    val userViewModel = remember { UserViewModel(repo = UserRepoImpl()) }

    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val activity = context as Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBackground, Color(0xFF1C2038)))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            // ── Brand badge ──
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Gold),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.NoteAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                "Create account",
                style = TextStyle(color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Start capturing your notes today",
                style = TextStyle(color = TextMuted, fontSize = 14.sp),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Form card ──
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    if (errorMessage != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ErrorBg, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = ErrorText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(errorMessage ?: "", style = TextStyle(color = ErrorText, fontSize = 13.sp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Full Name
                    Text("Full name", style = TextStyle(color = TextTagline, fontSize = 12.sp, fontWeight = FontWeight.Medium))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it; errorMessage = null },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Jane Doe", color = TextPlaceholder) },
                        singleLine = true,
                        colors = OutlinedTextFieldColorsDark(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Username
                    Text("Username", style = TextStyle(color = TextTagline, fontSize = 12.sp, fontWeight = FontWeight.Medium))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it; errorMessage = null },
                        leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("janedoe", color = TextPlaceholder) },
                        singleLine = true,
                        colors = OutlinedTextFieldColorsDark(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    Text("Email", style = TextStyle(color = TextTagline, fontSize = 12.sp, fontWeight = FontWeight.Medium))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("you@example.com", color = TextPlaceholder) },
                        singleLine = true,
                        colors = OutlinedTextFieldColorsDark(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    Text("Password", style = TextStyle(color = TextTagline, fontSize = 12.sp, fontWeight = FontWeight.Medium))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                        visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { visibility = !visibility }) {
                                Icon(
                                    painter = if (visibility)
                                        painterResource(id = R.drawable.outline_visibility_24)
                                    else
                                        painterResource(id = R.drawable.outline_visibility_off_24),
                                    contentDescription = null,
                                    tint = TextMuted,
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("At least 6 characters", color = TextPlaceholder) },
                        singleLine = true,
                        colors = OutlinedTextFieldColorsDark(),
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Button(
                        onClick = {
                            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                                errorMessage = "Please fill all fields"
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null

                            userViewModel.register(email, password) { success, msg, userId ->
                                if (success) {
                                    val model = UserModel(
                                        id = userId,
                                        name = fullName,
                                        email = email,
                                    )

                                    userViewModel.addUser(userId, model) { addSuccess, addMsg ->
                                        if (addSuccess) {
                                            // Save the chosen username as the Firebase display name so
                                            // it shows up as "Username" on the Profile screen.
                                            val profileUpdate = UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .build()
                                            FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdate)
                                                ?.addOnCompleteListener {
                                                    isLoading = false
                                                    Toast.makeText(context, "Registered successfully!", Toast.LENGTH_LONG).show()
                                                    context.startActivity(Intent(context, LoginActivity::class.java))
                                                    activity.finish()
                                                }
                                                ?: run {
                                                    isLoading = false
                                                    Toast.makeText(context, "Registered successfully!", Toast.LENGTH_LONG).show()
                                                    context.startActivity(Intent(context, LoginActivity::class.java))
                                                    activity.finish()
                                                }
                                        } else {
                                            isLoading = false
                                            errorMessage = addMsg ?: "Failed to save user data"
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    errorMessage = msg ?: "Registration failed"
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            disabledContainerColor = Gold.copy(alpha = 0.5f),
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(18.dp).width(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                "Create account",
                                style = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text("Already have an account? ", style = TextStyle(color = TextFaint, fontSize = 13.sp))
                Text(
                    "Sign in",
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        activity.finish()
                    },
                    style = TextStyle(color = Gold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview
@Composable
fun RegisterPreview() {
    NoteTakingTheme {
        RegisterBody()
    }
}