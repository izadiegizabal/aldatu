package xyz.izadi.aldatu.ui.components

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import org.junit.Rule
import org.junit.Test

class SquareCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_title_subtitle_displayed() {
        // Start the app
        composeTestRule.setContent {
            BaseContainer {
                SquareCard(title = "USD", subtitle = "1.23")
            }
        }
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("1.23").assertIsDisplayed()
    }

    @Test
    fun test_title_subtitle_not_displayed() {
        // Start the app
        composeTestRule.setContent {
            BaseContainer {
                SquareCard(title = null, subtitle = null)
            }
        }

        composeTestRule.onRoot().onChildren().assertCountEquals(0)
    }
}