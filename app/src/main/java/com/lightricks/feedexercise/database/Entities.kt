package com.lightricks.feedexercise.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_items")
data class FeedItemEntity(
    @ColumnInfo val configuration: String,
    @PrimaryKey val feedId: String,
    @ColumnInfo val isNew: Boolean,
    @ColumnInfo val isPremium: Boolean,
    @ColumnInfo val templateName: String,
    @ColumnInfo val templateThumbnailURI: String
)
