package com.lightricks.feedexercise.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.lightricks.feedexercise.ui.feed.FeedViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class FeedViewModelTest {
    @get:Rule
    val instantLiveData = InstantTaskExecutorRule()

    private val listOfFeedItems = createSampleListOfFeedItems()
    private lateinit var viewModelTest: FeedViewModel
    private lateinit var viewModelWithErrorTest: FeedViewModel

    @Mock
    private lateinit var mockFeedRepository: Repository

    @Mock
    private lateinit var mockErrorFeedRepository: Repository

    @Before
    fun init() {
        setUpMockRepositories()
        viewModelTest = FeedViewModel(mockFeedRepository)
        viewModelWithErrorTest = FeedViewModel(mockErrorFeedRepository)
    }

    private fun setUpMockRepositories() {
        `when`(mockFeedRepository.getFeed()).thenReturn(Observable.just(listOfFeedItems))
        `when`(mockFeedRepository.refresh()).thenReturn(Completable.complete())

        `when`(mockErrorFeedRepository.getFeed()).thenReturn(Observable.just(emptyList()))
        `when`(mockErrorFeedRepository.refresh()).thenReturn(
            Completable.error(
                Throwable(
                    ERROR_MESSAGE
                )
            )
        )
    }

    private fun createSampleListOfFeedItems(): List<FeedItem> {
        return listOf(
            FeedItem(
                id = "1",
                isPremium = true,
                thumbnailUrl = "11111.jpg"
            ),
            FeedItem(
                id = "2",
                isPremium = true,
                thumbnailUrl = "ok.jpeg"
            ),
            FeedItem(
                id = "3",
                isPremium = false,
                thumbnailUrl = "aaa.png"
            ),
        )
    }

    @Test
    fun whenRefreshCalledInViewModel_afterModelInit_thenRefreshWasCalledTwiceInRepository() {
        viewModelTest.refresh()
        //happens once for the init of the view model
        verify(mockFeedRepository, times(2)).refresh()
    }

    @Test
    fun whenCallRefresh_isLoadingWasFalse_thenIsLoadingIsTrue() {
        assertFalse(viewModelTest.getIsLoading().testObserver().requireCurrentValue())

        viewModelTest.getIsLoading().testObserver().assertValuesByInvocation(
            expectedValues = arrayOf(true),
            invocation = { viewModelTest.refresh() })
    }

    @Test
    fun whenCallIsEmpty_repositoryNotEmpty_thenShouldReturnFalse() {
        assertFalse(viewModelTest.getIsEmpty().testObserver().requireCurrentValue())
        assertTrue(viewModelWithErrorTest.getIsEmpty().testObserver().requireCurrentValue())
    }

    @Test
    fun whenCallGetFeedItems_repositoryHasItems_thenShouldReturnRepositoryFeed() {
        val actualFullList = viewModelTest.getFeedItems().testObserver().requireCurrentValue()
        val actualEmptyList =
            viewModelWithErrorTest.getFeedItems().testObserver().requireCurrentValue()

        assertEquals(listOfFeedItems, actualFullList)
        assertEquals(emptyList<FeedItem>(), actualEmptyList)
    }

    @Test
    fun whenCallGetNetworkErrorEvent_bothCases_thenShouldReturnCorrectEvent() {
        // view model without error
        viewModelTest.getNetworkErrorEvent().testObserver().assertEmpty()

        // view model with error
        val actualEventError =
            viewModelWithErrorTest.getNetworkErrorEvent().testObserver().requireCurrentValue()
        assertEquals(ERROR_MESSAGE, actualEventError.getContentIfNotHandled())
    }

    companion object {
        private const val ERROR_MESSAGE = "Test Error"
    }
}


class TestObserver<T> : Observer<T> {
    private val emittedValuesInternal = mutableListOf<T>()
    private val emittedValues: List<T>
        get() = emittedValuesInternal.toList()

    override fun onChanged(t: T) {
        emittedValuesInternal.add(t)
    }

    fun getCurrentValue(): T? {
        return emittedValues.lastOrNull()
    }

    fun requireCurrentValue(): T {
        return getCurrentValue()!!
    }

    fun assertEmpty() {
        assertThat(emittedValuesInternal).isEmpty()
    }

    fun assertValues(vararg expectedValues: T) {
        assertThat(emittedValuesInternal).containsExactly(*expectedValues)
    }

    fun assertValuesByInvocation(vararg expectedValues: T, invocation: () -> Unit) {
        val oldValueCount = emittedValuesInternal.size
        invocation()
        val newValues = emittedValuesInternal.drop(oldValueCount)
        assertThat(newValues).isEqualTo(listOf(*expectedValues))
    }

    fun reset() {
        emittedValuesInternal.clear()
    }

}

fun <T> LiveData<T>.testObserver() = TestObserver<T>().also { observeForever(it) }
