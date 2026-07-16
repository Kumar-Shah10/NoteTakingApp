package com.example.notetaking

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

// Test account credentials — this must be a real user that already exists in
// your Firebase project's Authentication > Sign-in method > Email/Password.
// Create it once (via the Firebase console, or by signing up through the app)
// and reuse it for all instrumented tests.
private const val TEST_EMAIL = "shahkumar2061@gmail.com"
private const val TEST_PASSWORD = "123456"

@RunWith(AndroidJUnit4::class)
class HomeNavigationTest {

    // createEmptyComposeRule() does NOT auto-launch an activity, unlike
    // createAndroidComposeRule<HomeActivity>() — this lets us sign in first.
    @get:Rule
    val composeRule = createEmptyComposeRule()

    private lateinit var scenario: ActivityScenario<HomeActivity>

    @Before
    fun setup() {
        signInTestUser()
        Intents.init()
        scenario = ActivityScenario.launch(HomeActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
        Intents.release()
        FirebaseAuth.getInstance().signOut()
    }

    @Test
    fun tappingProfileAvatar_opensProfileActivity() {
        composeRule.onNodeWithTag("profile_avatar")
            .performClick()

        Intents.intended(hasComponent(ProfileActivity::class.java.name))
    }

    private fun signInTestUser() {
        val latch = CountDownLatch(1)
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)
            .addOnCompleteListener { latch.countDown() }

        val signedInInTime = latch.await(15, TimeUnit.SECONDS)
        check(signedInInTime) { "Sign-in timed out — check test device network and Firebase project config." }
        check(FirebaseAuth.getInstance().currentUser != null) {
            "Sign-in failed — check that the test account exists and credentials are correct."
        }
    }
}