package com.lightricks.feedexercise.network

import retrofit2.http.GET
import retrofit2.Retrofit

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.rxjava3.core.Single
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

interface FeedApiService {
    @GET(FEED_QUERY)
    fun getFeed(): Single<FeedMetadataDto>

    companion object {
        private const val FEED_QUERY = "feed.json"
        private const val BASE_URL = "https://assets.swishvideoapp.com/Android/demo/"
        const val THUMBNAILS_URL_PREFIX = BASE_URL + "catalog/thumbnails/"

        private val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val feedApiService: FeedApiService = retrofit.create(FeedApiService::class.java)
    }
}
