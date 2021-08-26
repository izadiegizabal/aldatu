package xyz.izadi.aldatu.ui.components

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import org.junit.Rule
import org.junit.Test

class HeaderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_title_subtitle_displayed() {
        // Start the app
        composeTestRule.setContent {
            BaseContainer {
                Header(title = "Title", subtitle = "Subtitle")
            }
        }
        composeTestRule.onNodeWithText("Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Subtitle").assertIsDisplayed()
    }

    @Test
    fun test_title_subtitle_not_displayed() {
        // Start the app
        composeTestRule.setContent {
            BaseContainer {
                Header(title = null, subtitle = null)
            }
        }

        composeTestRule.onRoot().onChildren().assertCountEquals(0)
    }
}