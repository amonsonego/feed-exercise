package com.lightricks.feedexercise.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_items")
data class FeedItemEntity(
    @ColumnInfo (name = "configuration") val configuration: String,
    @ColumnInfo (name ="id") @PrimaryKey  val feedId: String,
    @ColumnInfo (name ="isNew") val isNew: Boolean,
    @ColumnInfo (name ="isPremium") val isPremium: Boolean,
    @ColumnInfo (name ="templateName") val templateName: String,
    @ColumnInfo (name ="templateThumbnailURI") val templateThumbnailURI: String
)
