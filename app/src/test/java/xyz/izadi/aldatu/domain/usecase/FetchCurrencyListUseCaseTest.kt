package xyz.izadi.aldatu.domain.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.repository.Result
import xyz.izadi.aldatu.domain.repository.CurrencyRepository
import xyz.izadi.aldatu.domain.repository.ErrorWhileFetchingException
import xyz.izadi.aldatu.domain.repository.NoInternetConnectionException
import xyz.izadi.aldatu.testUtils.CoroutineTestRule

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FetchCurrencyListUseCaseTest : TestCase() {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    lateinit var currencyRepo: CurrencyRepository

    private lateinit var sut: FetchCurrencyListUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        sut = FetchCurrencyListUseCase(currencyRepo)
    }

    @Test
    fun fetch_successful() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        val mockCurrencyList = listOf(
            Currency("USD", "Dollar"),
            Currency("EUR", "Euro")
        )
        `when`(currencyRepo.getCurrencies()).thenReturn(mockCurrencyList)

        // act
        val actual = sut.invoke().toList()

        // assert
        assertEquals(listOf(Result.loading(), Result.success(mockCurrencyList)), actual)
    }

    @Test
    fun fetch_error_no_internet() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        `when`(currencyRepo.getCurrencies()).thenThrow(NoInternetConnectionException())

        // act
        val actual = sut.invoke().toList()

        // assert
        assertEquals(
            listOf<Result<List<Currency>>>(
                Result.loading(),
                Result.error("NoInternetConnectionException")
            ), actual
        )
    }

    @Test
    fun fetch_error_while_fetching() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        `when`(currencyRepo.getCurrencies()).thenThrow(ErrorWhileFetchingException("No Auth", 401))

        // act
        val actual = sut.invoke().toList()

        // assert
        assertEquals(
            listOf<Result<List<Currency>>>(
                Result.loading(),
                Result.error("No Auth", 401)
            ), actual
        )
    }

    @Test
    fun fetch_error_generic() = coroutineTestRule.testDispatcher.runBlockingTest {
        // arrange
        `when`(currencyRepo.getCurrencies()).thenThrow(NullPointerException("test"))

        // act
        val actual = sut.invoke().toList()

        // assert
        assertEquals(
            listOf<Result<List<Currency>>>(
                Result.loading(),
                Result.error("test")
            ), actual
        )
    }
}