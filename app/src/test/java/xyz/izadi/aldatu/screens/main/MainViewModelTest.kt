package xyz.izadi.aldatu.screens.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.PreferencesManager
import xyz.izadi.aldatu.domain.usecase.FetchCurrencyListUseCase
import xyz.izadi.aldatu.domain.usecase.FetchCurrencyRatesUseCase
import xyz.izadi.aldatu.testUtils.CoroutineTestRule
import xyz.izadi.aldatu.utils.Constants
import xyz.izadi.aldatu.utils.setValue

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest : TestCase() {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    lateinit var fetchCurrencyListUseCase: FetchCurrencyListUseCase

    @Mock
    lateinit var fetchCurrencyRatesUseCase: FetchCurrencyRatesUseCase

    @Mock
    lateinit var preferencesManager: PreferencesManager

    private lateinit var sut: MainViewModel

    @Before
    fun setup() = coroutineTestRule.testDispatcher.runBlockingTest {
        MockitoAnnotations.openMocks(this)

        // to not have an coroutine exception
        `when`(fetchCurrencyListUseCase.invoke()).thenReturn(flowOf())
        `when`(fetchCurrencyRatesUseCase.invoke(any())).thenReturn(flowOf())

        sut = MainViewModel(fetchCurrencyListUseCase, fetchCurrencyRatesUseCase, preferencesManager)
    }

    @Test
    fun validate_default_values() {
        // arrange
        `when`(preferencesManager.getDefaultAmount()).thenReturn(Constants.DEFAULT_AMOUNT)
        `when`(preferencesManager.getDefaultCurrency()).thenReturn(Constants.DEFAULT_CURRENCY)

        // assert
        assertEquals(null, sut.refreshDate.value)
        assertEquals(null, sut.currencyList.value)
        assertEquals(null, sut.currencyRatesState.value)
        assertEquals(null, sut.currencyRates.value)
        assertEquals(Constants.DEFAULT_CURRENCY, sut.currentCurrency.value)
        assertEquals(Constants.DEFAULT_AMOUNT, sut.currentAmount.value)
        assertEquals(null, sut.rates.value)
    }

    @Test
    fun validate_set_new_amount() {
        // arrange
        val newValueString = "345"

        // act
        sut.setNewAmount(newValueString)

        // assert
        assertEquals(newValueString.toFloatOrNull(), sut.currentAmount.value)
        verify(preferencesManager).setDefaultAmount(newValueString.toFloat())
    }

    @Test
    fun validate_select_currency() {
        // arrange
        val newCurrency = Currency("USD", "Dollars")

        // act
        sut.setNewCurrency(newCurrency)

        // assert
        assertEquals(newCurrency, sut.currentCurrency.value)
        verify(preferencesManager).setDefaultCurrency(newCurrency)
    }

    @Test
    fun validate_get_conversion_existing() {
        // arrange
        `when`(preferencesManager.getDefaultAmount()).thenReturn(1f)
        val mockFrom = Currency("EUR", "Euro")
        val sampleMap = hashMapOf(Pair("USDEUR", 0.85), Pair("USDJPY", 109.22))
        sut.rates.setValue(sampleMap)

        // act
        val actual = sut.getConversion(from = mockFrom, to = "JPY")

        // assert
        assertEquals(128.49411.toString(), actual.toString())

    }

    @Test
    fun validate_get_conversion_non_existing() {
        // arrange
        `when`(preferencesManager.getDefaultAmount()).thenReturn(1f)
        val mockFrom = Currency("EUR", "Euro")
        val sampleMap = hashMapOf(Pair("USDEUR", 0.85), Pair("USDJPY", 109.22))
        sut.rates.setValue(sampleMap)

        // act
        val actual = sut.getConversion(from = mockFrom, to = "GBP")

        // assert
        assertEquals(null, actual)
    }
}