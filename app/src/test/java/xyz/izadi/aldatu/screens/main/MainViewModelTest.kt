package xyz.izadi.aldatu.screens.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.PreferencesManager
import xyz.izadi.aldatu.data.repository.CurrencyRepository
import xyz.izadi.aldatu.utils.Constants

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest : TestCase() {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var currencyRepository: CurrencyRepository

    @Mock
    lateinit var preferencesManager: PreferencesManager

    private lateinit var sut: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun validate_default_values() {
        // arrange
        `when`(preferencesManager.getDefaultAmount()).thenReturn(Constants.DEFAULT_AMOUNT)
        `when`(preferencesManager.getDefaultCurrency()).thenReturn(Constants.DEFAULT_CURRENCY)
        sut = MainViewModel(currencyRepository, preferencesManager)

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
        sut = MainViewModel(currencyRepository, preferencesManager)
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
        sut = MainViewModel(currencyRepository, preferencesManager)
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
        `when`(preferencesManager.getDefaultCurrency()).thenReturn(mockFrom)
        sut = MainViewModel(currencyRepository, preferencesManager)
        val sampleMap = hashMapOf(Pair("USDEUR", 0.85), Pair("USDJPY", 109.22))
        sut.rates.value = sampleMap

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
        `when`(preferencesManager.getDefaultCurrency()).thenReturn(mockFrom)
        sut = MainViewModel(currencyRepository, preferencesManager)
        val sampleMap = hashMapOf(Pair("USDEUR", 0.85), Pair("USDJPY", 109.22))
        sut.rates.value = sampleMap

        // act
        val actual = sut.getConversion(from = mockFrom, to = "GBP")

        // assert
        assertEquals(null, actual)
    }
}