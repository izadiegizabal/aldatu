package xyz.izadi.aldatu.screens.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.AndroidEntryPoint
import xyz.izadi.aldatu.ui.components.CurrencyFab
import xyz.izadi.aldatu.ui.components.CurrencySelector
import xyz.izadi.aldatu.ui.components.MainView
import xyz.izadi.aldatu.ui.theme.AldatuTheme

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
    val currentCurrency by vm.currentCurrency.observeAsState()
    val currentAmount by vm.currentAmount.observeAsState()

    val currencySelectorState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val swipeRefreshState = rememberSwipeRefreshState(false)
    val scope = rememberCoroutineScope()

    AldatuTheme {
        ProvideWindowInsets {
            CurrencySelector(currencySelectorState, scope, vm) {
                Scaffold(
                    floatingActionButton = {
                        CurrencyFab(
                            scope = scope,
                            state = currencySelectorState,
                            selectedCurrency = currentCurrency,
                            selectedAmount = currentAmount
                        )
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) {
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
    }
}