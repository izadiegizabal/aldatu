package xyz.izadi.aldatu.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.samePropertyValuesAs
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyList
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.CurrencyListDao
import xyz.izadi.aldatu.data.local.CurrencyRate
import xyz.izadi.aldatu.data.local.CurrencyRatesDao
import xyz.izadi.aldatu.data.local.PreferencesManager
import xyz.izadi.aldatu.data.remote.CurrencyApi
import xyz.izadi.aldatu.data.remote.CurrencyListResponse
import xyz.izadi.aldatu.data.remote.CurrencyRatesResponse
import xyz.izadi.aldatu.domain.repository.CurrencyRepository
import xyz.izadi.aldatu.domain.repository.ErrorWhileFetchingException
import xyz.izadi.aldatu.domain.repository.NoInternetConnectionException
import xyz.izadi.aldatu.testUtils.CoroutineTestRule


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CurrencyRepositoryImplTest : TestCase() {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    lateinit var currencyApi: CurrencyApi

    @Mock
    lateinit var currencyListDao: CurrencyListDao

    @Mock
    lateinit var currencyRatesDao: CurrencyRatesDao

    @Mock
    lateinit var prefManager: PreferencesManager

    private lateinit var sut: CurrencyRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        sut = CurrencyRepositoryImpl(
            currencyApi,
            currencyListDao,
            currencyRatesDao,
            prefManager
        )
    }

    @Test
    fun fetch_currency_list_local() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        val mockCurrencyList = listOf(
            Currency("USD", "Dollar"),
            Currency("EUR", "Euro")
        )
        `when`(currencyListDao.doWeHaveCurrencies()).thenReturn(true)
        `when`(currencyListDao.loadCurrencyList()).thenReturn(mockCurrencyList)

        // act
        val actual = sut.getCurrencies()

        // assert
        verify(currencyApi, times(0)).getSupportedCountries()
        verify(currencyListDao, times(0)).saveCurrencies(anyList())
        verify(currencyListDao, times(1)).loadCurrencyList()
        assertEquals(mockCurrencyList, actual)
    }

    @Test
    fun fetch_currency_list_remote_success() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        val mockCurrencyList = listOf(
            Currency("USD", "Dollar"),
            Currency("EUR", "Euro")
        )
        val mockCurrencyListResponse = CurrencyListResponse().apply {
            currenciesMap =
                mockCurrencyList.map { it.currencyCode to (it.currencyName ?: "") }.toMap()
        }
        val mockResponse = Response.success(mockCurrencyListResponse)
        `when`(currencyListDao.doWeHaveCurrencies()).thenReturn(false)
        `when`(currencyApi.getSupportedCountries()).thenReturn(mockResponse)

        // act
        val actual = sut.getCurrencies()

        // assert
        verify(currencyApi, times(1)).getSupportedCountries()
        verify(currencyListDao, times(1)).saveCurrencies(anyList())
        verify(currencyListDao, times(0)).loadCurrencyList()
        assertEquals(actual.size, mockCurrencyList.size)
        actual.forEachIndexed { index, currency ->
            assertThat(currency, samePropertyValuesAs(mockCurrencyList[index]))
        }
    }

    @Test
    fun fetch_currency_list_remote_error_no_internet() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            // arrange
            val mockError = NoInternetConnectionException()
            `when`(currencyListDao.doWeHaveCurrencies()).thenReturn(false)
            `when`(currencyApi.getSupportedCountries()).thenThrow(IOException(mockError))

            // act
            var actual: Exception? = null
            runCatching {
                sut.getCurrencies()
            }.onFailure {
                actual = it as Exception?
            }

            // assert
            verify(currencyApi, times(1)).getSupportedCountries()
            verify(currencyListDao, times(0)).saveCurrencies(anyList())
            verify(currencyListDao, times(0)).loadCurrencyList()
            assertEquals(mockError, actual)
        }

    @Test
    fun fetch_currency_list_remote_error_not_successful() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            // arrange

            val mockRes = Response.error<CurrencyListResponse>(401, "Error".toResponseBody())
            `when`(currencyListDao.doWeHaveCurrencies()).thenReturn(false)
            `when`(currencyApi.getSupportedCountries()).thenReturn(mockRes)

            // act
            var actual: Exception? = null
            runCatching {
                sut.getCurrencies()
            }.onFailure {
                actual = it as Exception?
            }

            // assert
            verify(currencyApi, times(1)).getSupportedCountries()
            verify(currencyListDao, times(0)).saveCurrencies(anyList())
            verify(currencyListDao, times(0)).loadCurrencyList()
            assertEquals(mockRes.message(), actual!!.message)
            assertEquals(mockRes.code(), (actual as ErrorWhileFetchingException).errorCode)
        }


    @Test
    fun fetch_currency_rates_local() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        val mockCurrencyRatesList = listOf(
            CurrencyRate("USDUSD", 1.0, 123L),
            CurrencyRate("USDEUR", 1.1, 123L)
        )
        `when`(prefManager.shouldRefreshRates()).thenReturn(false)
        `when`(currencyRatesDao.doWeHaveRates()).thenReturn(true)
        `when`(currencyRatesDao.loadCurrencyRates()).thenReturn(mockCurrencyRatesList)

        // act
        val actual = sut.getExchangeRates()

        // assert
        verify(currencyApi, times(0)).getCurrencyRates()
        verify(currencyRatesDao, times(0)).saveCurrencyRates(anyList())
        verify(prefManager, times(0)).saveRefreshDate()
        verify(currencyRatesDao, times(1)).loadCurrencyRates()
        assertEquals(mockCurrencyRatesList, actual)
    }

    @Test
    fun force_refresh_fetches_rates_from_remote() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            // arrange
            `when`(currencyApi.getCurrencyRates()).thenReturn(
                Response.success(CurrencyRatesResponse())
            )

            // act
            sut.getExchangeRates(true)

            // assert
            verify(currencyApi, times(1)).getCurrencyRates()
            verify(currencyRatesDao, times(1)).saveCurrencyRates(anyList())
            verify(prefManager, times(1)).saveRefreshDate()
            verify(currencyRatesDao, times(0)).loadCurrencyRates()
        }

    @Test
    fun preference_should_fetches_rates_from_remote() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            // arrange
            `when`(prefManager.shouldRefreshRates()).thenReturn(true)
            `when`(currencyApi.getCurrencyRates()).thenReturn(
                Response.success(CurrencyRatesResponse())
            )

            // act
            sut.getExchangeRates()

            // assert
            verify(currencyApi, times(1)).getCurrencyRates()
            verify(currencyRatesDao, times(1)).saveCurrencyRates(anyList())
            verify(prefManager, times(1)).saveRefreshDate()
            verify(currencyRatesDao, times(0)).loadCurrencyRates()
        }

    @Test
    fun no_local_fetches_rates_from_remote() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            // arrange
            `when`(currencyRatesDao.doWeHaveRates()).thenReturn(false)
            `when`(currencyApi.getCurrencyRates()).thenReturn(
                Response.success(CurrencyRatesResponse())
            )

            // act
            sut.getExchangeRates()

            // assert
            verify(currencyApi, times(1)).getCurrencyRates()
            verify(currencyRatesDao, times(1)).saveCurrencyRates(anyList())
            verify(prefManager, times(1)).saveRefreshDate()
            verify(currencyRatesDao, times(0)).loadCurrencyRates()
        }

    @Test
    fun fetch_currency_rates_remote_success() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        val mockCurrencyRatesList = listOf(
            CurrencyRate("USDUSD", 1.0, 123L),
            CurrencyRate("USDEUR", 1.1, 123L)
        )
        val mockCurrencyListResponse = CurrencyRatesResponse().apply {
            rates = mockCurrencyRatesList.map { it.currencyCode to it.rate }.toMap()
        }
        val mockResponse = Response.success(mockCurrencyListResponse)
        `when`(prefManager.shouldRefreshRates()).thenReturn(true)
        `when`(currencyApi.getCurrencyRates()).thenReturn(mockResponse)


        // act
        val actual = sut.getExchangeRates()

        // assert
        verify(currencyApi, times(1)).getCurrencyRates()
        verify(currencyRatesDao, times(1)).saveCurrencyRates(Mockito.anyList())
        verify(prefManager, times(1)).saveRefreshDate()
        verify(currencyRatesDao, times(0)).loadCurrencyRates()
        assertEquals(mockCurrencyRatesList.size, actual.size)
        actual.forEachIndexed { index, rate ->
            assertThat(rate, samePropertyValuesAs(mockCurrencyRatesList[index], "timestamp"))
        }
    }

    @Test
    fun fetch_currency_rates_remote_no_internet() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            // arrange
            val mockError = NoInternetConnectionException()
            `when`(prefManager.shouldRefreshRates()).thenReturn(true)
            `when`(currencyApi.getCurrencyRates()).thenThrow(IOException(mockError))


            // act
            var actual: Exception? = null
            runCatching {
                sut.getExchangeRates()
            }.onFailure {
                actual = it as Exception?
            }

            // assert
            verify(currencyApi, times(1)).getCurrencyRates()
            verify(currencyRatesDao, times(0)).saveCurrencyRates(Mockito.anyList())
            verify(prefManager, times(0)).saveRefreshDate()
            verify(currencyRatesDao, times(0)).loadCurrencyRates()
            assertEquals(mockError, actual)
        }

    @Test
    fun fetch_currency_rates_remote_error_return() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            // arrange
            val mockRes = Response.error<CurrencyRatesResponse>(401, "Error".toResponseBody())
            `when`(prefManager.shouldRefreshRates()).thenReturn(true)
            `when`(currencyApi.getCurrencyRates()).thenReturn(mockRes)


            // act
            var actual: Exception? = null
            runCatching {
                sut.getExchangeRates()
            }.onFailure {
                actual = it as Exception?
            }

            // assert
            verify(currencyApi, times(1)).getCurrencyRates()
            verify(currencyRatesDao, times(0)).saveCurrencyRates(Mockito.anyList())
            verify(prefManager, times(0)).saveRefreshDate()
            verify(currencyRatesDao, times(0)).loadCurrencyRates()
            assertEquals(mockRes.message(), actual!!.message)
            assertEquals(mockRes.code(), (actual as ErrorWhileFetchingException).errorCode)
        }


}