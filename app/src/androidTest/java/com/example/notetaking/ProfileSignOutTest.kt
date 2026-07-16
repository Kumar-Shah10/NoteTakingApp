package com.example.notetaking

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileSignOutTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ProfileActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun tappingSignOut_showsConfirmationDialog() {
        composeRule.onNodeWithTag("sign_out_row")
            .performClick()

        composeRule.onNodeWithText("Sign out?").assertExists()
    }

    @Test
    fun confirmingSignOut_navigatesToLoginActivity() {
        // Open the sign out dialog
        composeRule.onNodeWithTag("sign_out_row")
            .performClick()

        // Click the SECOND "Sign out" node (the dialog button)
        composeRule.onAllNodesWithText("Sign out")[1]
            .performClick()

        Intents.intended(hasComponent(LoginActivity::class.java.name))
    }
}