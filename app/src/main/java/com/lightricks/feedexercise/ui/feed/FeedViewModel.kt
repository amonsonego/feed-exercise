package com.lightricks.feedexercise.ui.feed

import androidx.lifecycle.*
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.data.Repository
import com.lightricks.feedexercise.util.Event
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.IllegalArgumentException
import java.util.function.Supplier

/**
 * This view model manages the data for [FeedFragment].
 */
class FeedViewModel(private val feedRepository: Repository) : ViewModel() {
    private val stateInternal: MutableLiveData<State> = MutableLiveData<State>(DEFAULT_STATE)
    private val networkErrorEvent = MutableLiveData<Event<String>>()
    private val compositeDisposable = CompositeDisposable()
    private var refreshFeedItemsDisposable: Disposable? = null

    init {
        refresh()
        observeFeedItems()
    }

    fun refresh() {
        if (!getState().isLoading) {
            updateState { copy(isLoading = true) }
            refreshFeedItemsDisposable = feedRepository.refresh()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        updateState { copy(isLoading = false) }
                    }, { error ->
                        updateState { copy(isLoading = false) }
                        networkErrorEvent.value =
                            Event(error.localizedMessage ?: "no error message")
                    })
        }
    }

    private fun observeFeedItems() {
        compositeDisposable.add(
            feedRepository.getFeed().observeOn(AndroidSchedulers.mainThread())
                .subscribe { feedItems ->
                    updateState { copy(feedItems = feedItems) }
                })
    }

    fun getIsLoading(): LiveData<Boolean> {
        return Transformations.map(stateInternal) { it.isLoading }
    }

    fun getIsEmpty(): LiveData<Boolean> {
        return Transformations.map(stateInternal) { it.feedItems?.isEmpty() ?: true }
    }

    fun getFeedItems(): LiveData<List<FeedItem>> {
        return Transformations.map(stateInternal) { it.feedItems ?: emptyList() }
    }

    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    private fun updateState(transform: State.() -> State) {
        stateInternal.value = transform(getState())
    }

    private fun getState(): State {
        return stateInternal.value!!
    }

    override fun onCleared() {
        refreshFeedItemsDisposable?.dispose()
        compositeDisposable.dispose()
    }

    data class State(
        val feedItems: List<FeedItem>?,
        val isLoading: Boolean,
    )

    companion object {
        private val DEFAULT_STATE = State(
            feedItems = null,
            isLoading = false,
        )
    }
}

/**
 * This class creates instances of [FeedViewModel].
 * It's not necessary to use this factory at this stage. But if we will need to inject
 * dependencies into [FeedViewModel] in the future, then this is the place to do it.
 */
class FeedViewModelFactory(private val feedRepository: Supplier<FeedRepository>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            throw IllegalArgumentException("factory used with a wrong class")
        }
        @Suppress("UNCHECKED_CAST")
        return FeedViewModel(feedRepository.get()) as T
    }

}
