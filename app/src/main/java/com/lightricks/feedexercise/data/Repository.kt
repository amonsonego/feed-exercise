package com.lightricks.feedexercise.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface Repository {
    fun getFeed(): Observable<List<FeedItem>>
    fun refresh(): Completable
}
