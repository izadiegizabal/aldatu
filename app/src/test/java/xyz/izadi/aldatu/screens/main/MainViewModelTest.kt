package xyz.izadi.aldatu.screens.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.CurrencyRate
import xyz.izadi.aldatu.data.local.PreferencesManager
import xyz.izadi.aldatu.data.repository.Result
import xyz.izadi.aldatu.domain.usecase.FetchCurrencyListUseCase
import xyz.izadi.aldatu.domain.usecase.FetchCurrencyRatesUseCase
import xyz.izadi.aldatu.testUtils.CoroutineTestRule
import xyz.izadi.aldatu.testUtils.getOrAwaitValue
import xyz.izadi.aldatu.utils.Constants
import xyz.izadi.aldatu.utils.getFormattedString
import xyz.izadi.aldatu.utils.setValue
import java.util.*

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

    @Mock
    lateinit var currencyRatesStateObserver: Observer<Result<List<CurrencyRate>>?>

    private lateinit var sut: MainViewModel

    @Before
    fun setup() = coroutineTestRule.testDispatcher.runBlockingTest {
        MockitoAnnotations.openMocks(this)

        // to not have an coroutine exception
        `when`(fetchCurrencyListUseCase.invoke()).thenReturn(flowOf())
        `when`(fetchCurrencyRatesUseCase.invoke(any())).thenReturn(flowOf())

        sut = MainViewModel(fetchCurrencyListUseCase, fetchCurrencyRatesUseCase, preferencesManager)

        sut.currencyRatesState.observeForever(currencyRatesStateObserver)
    }

    @Test
    fun validate_default_values_init() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        `when`(preferencesManager.getDefaultAmount()).thenReturn(Constants.DEFAULT_AMOUNT)
        `when`(preferencesManager.getDefaultCurrency()).thenReturn(Constants.DEFAULT_CURRENCY)

        // assert
        verify(fetchCurrencyListUseCase, times(1)).invoke(any())
        verify(fetchCurrencyRatesUseCase, times(1)).invoke(any())
        assertEquals(null, sut.refreshDate.value)
        assertEquals(null, sut.currencyList.value)
        assertEquals(null, sut.currencyRatesState.value)
        assertEquals(null, sut.currencyRates.value)
        assertEquals(Constants.DEFAULT_CURRENCY, sut.currentCurrency.value)
        assertEquals(Constants.DEFAULT_AMOUNT, sut.currentAmount.value)
        assertEquals(null, sut.rates.value)
    }

    @Test
    fun validate_load_currencies_success() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        val mockCurrencyList = listOf(
            Currency("USD", "Dollar"),
            Currency("EUR", "Euro")
        )
        val mockCurrencyRatesList = listOf(
            CurrencyRate("USDUSD", 1.0, 123L),
            CurrencyRate("USDEUR", 1.1, 123L)
        )
        val mockDate = Date()

        reset(fetchCurrencyListUseCase, fetchCurrencyRatesUseCase)
        `when`(fetchCurrencyListUseCase.invoke()).thenReturn(flowOf(Result.success(mockCurrencyList)))
        `when`(fetchCurrencyRatesUseCase.invoke(any())).thenReturn(
            flowOf(
                Result.success(
                    mockCurrencyRatesList
                )
            )
        )
        `when`(preferencesManager.getRefreshDate()).thenReturn(mockDate)

        // assert
        sut.loadCurrencies()

        // assert
        assertEquals(mockCurrencyList, sut.currencyList.getOrAwaitValue())
        assertEquals(
            Result.success(mockCurrencyRatesList),
            sut.currencyRatesState.getOrAwaitValue()
        )
        assertEquals(mapOf("USDEUR" to 1.1, "USDUSD" to 1.0), sut.rates.getOrAwaitValue())
        assertEquals(mockDate.getFormattedString(), sut.refreshDate.getOrAwaitValue())
        assertEquals(mockCurrencyRatesList, sut.currencyRates.getOrAwaitValue())
    }

    @Test
    fun validate_load_currencies_loading() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        reset(fetchCurrencyListUseCase, fetchCurrencyRatesUseCase)
        `when`(fetchCurrencyListUseCase.invoke()).thenReturn(flowOf(Result.loading()))
        `when`(fetchCurrencyRatesUseCase.invoke(anyBoolean())).thenReturn(flowOf(Result.loading()))

        // assert
        sut.loadCurrencies()

        // assert
        assertEquals(null, sut.currencyList.getOrAwaitValue())
        verify(currencyRatesStateObserver, times(1)).onChanged(Result.loading())
        assertEquals(null, sut.rates.getOrAwaitValue())
        assertEquals(null, sut.refreshDate.getOrAwaitValue())
        assertEquals(null, sut.currencyRates.getOrAwaitValue())
    }

    @Test
    fun validate_load_currencies_error() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        reset(fetchCurrencyListUseCase, fetchCurrencyRatesUseCase)
        `when`(fetchCurrencyListUseCase.invoke()).thenReturn(
            flowOf(
                Result.error(
                    "No Internet",
                    503
                )
            )
        )
        `when`(fetchCurrencyRatesUseCase.invoke(anyBoolean())).thenReturn(
            flowOf(
                Result.error(
                    "Test",
                    400
                )
            )
        )

        // assert
        sut.loadCurrencies()

        // assert
        assertEquals(null, sut.currencyList.getOrAwaitValue())
        verify(currencyRatesStateObserver, times(1)).onChanged(Result.error("Test", 400))
        assertEquals(null, sut.rates.getOrAwaitValue())
        assertEquals(null, sut.refreshDate.getOrAwaitValue())
        assertEquals(null, sut.currencyRates.getOrAwaitValue())
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

    @Test
    fun validate_set_new_amount() {
        // arrange
        val newValueString = "345"

        // act
        sut.setNewAmount(newValueString)

        // assert
        assertEquals(newValueString.toFloatOrNull(), sut.currentAmount.getOrAwaitValue())
        verify(preferencesManager).setDefaultAmount(newValueString.toFloat())
    }

    @Test
    fun validate_select_currency() {
        // arrange
        val newCurrency = Currency("USD", "Dollars")

        // act
        sut.setNewCurrency(newCurrency)

        // assert
        assertEquals(newCurrency, sut.currentCurrency.getOrAwaitValue())
        verify(preferencesManager).setDefaultCurrency(newCurrency)
    }
}