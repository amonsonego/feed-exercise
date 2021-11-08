package com.lightricks.feedexercise.data

import com.lightricks.feedexercise.database.FeedDao
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.FeedItemDto
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
class FeedRepository(
    private val feedApiService: FeedApiService,
    private val feedDao: FeedDao
) : Repository {

    /**
     * returns observable that cannot fail, no error can be expected
     */
    override fun getFeed(): Observable<List<FeedItem>> {
        return feedDao.getAllAsync()
            .subscribeOn(Schedulers.io())
            .map { it.toFeedItems() }
    }

    override fun refresh(): Completable {
        return feedApiService.getFeed()
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { feedResponse ->
                feedDao.refreshDatabaseAsync(feedResponse.templatesMetadata.toFeedEntities())
            }
    }
}

fun List<FeedItemEntity>.toFeedItems(): List<FeedItem> {
    return map { feedItemEntity ->
        FeedItem(
            id = feedItemEntity.feedId,
            thumbnailUrl = FeedApiService.THUMBNAILS_URL_PREFIX + feedItemEntity.templateThumbnailURI,
            isPremium = feedItemEntity.isPremium
        )
    }
}

fun List<FeedItemDto>.toFeedEntities(): List<FeedItemEntity> {
    return map { feedItemDto ->
        FeedItemEntity(
            configuration = feedItemDto.configuration,
            feedId = feedItemDto.id,
            isNew = feedItemDto.isNew,
            isPremium = feedItemDto.isPremium,
            templateName = feedItemDto.templateName,
            templateThumbnailURI = feedItemDto.templateThumbnailURI
        )
    }
}
