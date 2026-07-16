package com.example.notetaking

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteCreationInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<HomeActivity>()

    @Test
    fun creatingNote_appearsInRecentNotesList() {
        // Open the create-note dialog.
        composeRule.onNodeWithTag("add_note_fab")
            .performClick()

        // Fill in title and content.
        composeRule.onNodeWithTag("note_title_field")
            .performTextInput("Accessories")
        composeRule.onNodeWithTag("note_content_field")
            .performTextInput("Monitor, Keyboard, Mouse")

        // Save.
        composeRule.onNodeWithTag("note_save_button")
            .performClick()

        // The new note's title should now show up on the Home screen.
        composeRule.onNodeWithText("Accessories").assertExists()
    }
}