package com.lightricks.feedexercise.database

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class FeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(feedItems: List<FeedItemEntity>)

    @Delete
    abstract fun deleteAsync(feedItem: FeedItemEntity): Completable

    @Query("DELETE FROM feed_items")
    abstract fun deleteAll()

    @Query("SELECT * FROM feed_items")
    abstract fun getAllAsync(): Observable<List<FeedItemEntity>>

    @Query("SELECT count(*) FROM feed_items")
    abstract fun countAsync(): Single<Int>

    @Transaction
    open fun refreshDatabase(feedItems: List<FeedItemEntity>){
        deleteAll()
        insertAll(feedItems)
    }

    fun refreshDatabaseAsync(feedItems: List<FeedItemEntity>) :Completable{
        return Completable.fromAction{ refreshDatabase(feedItems) }
    }

}
