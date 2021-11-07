package com.lightricks.feedexercise.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lightricks.feedexercise.database.FeedDao
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.FeedItemDto
import com.lightricks.feedexercise.network.FeedMetadataDto
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class FeedRepositoryTest {
    @get:Rule
    val instantLiveData = InstantTaskExecutorRule()

    private lateinit var repositoryToTest: FeedRepository
    private val listOfDtoItems = createSampleListOfItemDto()
    private val metaDataDto = createMetadataDto(listOfDtoItems)

    @Mock
    private lateinit var mockFeedDao: FeedDao

    @Mock
    private lateinit var mockApiService: FeedApiService

    @Before
    fun init() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        initMockFeedDao()
        initMockApiService()
        repositoryToTest = FeedRepository(mockApiService, mockFeedDao)
    }

    private fun initMockFeedDao() {
        `when`(mockFeedDao.getAllAsync())
            .thenReturn(Observable.just(listOfDtoItems.toFeedEntities()))
    }

    private fun initMockApiService() {
        `when`(mockApiService.getFeed())
            .thenReturn(Single.just(metaDataDto))
    }

    private fun createMetadataDto(listOfItems: List<FeedItemDto>): FeedMetadataDto {
        return FeedMetadataDto(listOfItems)
    }

    private fun createSampleListOfItemDto(): List<FeedItemDto> {
        return listOf(
            FeedItemDto(
                configuration = " ",
                id = "1",
                isNew = false,
                isPremium = true,
                templateCategories = emptyList(),
                templateName = "1",
                templateThumbnailURI = ""
            ),
            FeedItemDto(
                configuration = " ",
                id = "2",
                isNew = false,
                isPremium = true,
                templateCategories = emptyList(),
                templateName = "1",
                templateThumbnailURI = ""
            ),
            FeedItemDto(
                configuration = " ",
                id = "3",
                isNew = false,
                isPremium = true,
                templateCategories = emptyList(),
                templateName = "1",
                templateThumbnailURI = ""
            ),
            FeedItemDto(
                configuration = " ",
                id = "4",
                isNew = false,
                isPremium = true,
                templateCategories = emptyList(),
                templateName = "1",
                templateThumbnailURI = ""
            )
        )
    }

    @Test
    fun refresh_shouldFillDBWithItemsFetchFromNetwork() {
        repositoryToTest.refresh()
            .test()
            .assertComplete()
            .assertNoErrors()

        verify(mockApiService).getFeed()
        verify(mockFeedDao).refreshDatabaseAsync(listOfDtoItems.toFeedEntities())
    }

    @Test
    fun getFeed_whileDbNotEmpty_shouldReturnCachedItem() {
        val actualListFromRepository = repositoryToTest.getFeed().blockingFirst()
        val expectedList = listOfDtoItems.toFeedEntities().toFeedItems()

        verify(mockFeedDao).getAllAsync()
        assertEquals(expectedList, actualListFromRepository)
    }
}
