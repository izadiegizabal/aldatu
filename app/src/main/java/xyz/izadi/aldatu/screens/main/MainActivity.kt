package xyz.izadi.aldatu.screens.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.AndroidEntryPoint
import xyz.izadi.aldatu.screens.main.ui.MainView
import xyz.izadi.aldatu.ui.components.BaseContainer
import xyz.izadi.aldatu.ui.components.currencyselector.CurrencySelector

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MainScreen(vm: MainViewModel = hiltViewModel()) {
    val currencySelectorState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val swipeRefreshState = rememberSwipeRefreshState(false)
    val scope = rememberCoroutineScope()

    BaseContainer {
        CurrencySelector(currencySelectorState, scope, vm) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { vm.loadCurrencies(true) },
            ) {
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    MainView(vm = vm)
                }
            }
        }
    }
}