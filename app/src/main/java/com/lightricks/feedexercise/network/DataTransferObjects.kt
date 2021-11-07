package com.lightricks.feedexercise.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedMetadataDto(
    @Json(name ="templatesMetadata")
    val templatesMetadata: List<FeedItemDto>
)

@JsonClass(generateAdapter = true)
data class FeedItemDto(
    @Json(name = "configuration")
    val configuration: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "isNew")
    val isNew: Boolean,
    @Json(name = "isPremium")
    val isPremium: Boolean,
    @Json(name = "templateCategories")
    val templateCategories: List<String>,
    @Json(name = "templateName")
    val templateName: String,
    @Json(name = "templateThumbnailURI")
    val templateThumbnailURI: String
)
