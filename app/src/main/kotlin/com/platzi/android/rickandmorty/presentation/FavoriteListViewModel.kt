package com.platzi.android.rickandmorty.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.platzi.android.rickandmorty.domain.Character
import com.platzi.android.rickandmorty.usecases.GetAllFavoriteCharacters
import io.reactivex.disposables.CompositeDisposable

class FavoriteListViewModel(private val getAllFavoriteCharacters: GetAllFavoriteCharacters): ViewModel() {

    private val disposable = CompositeDisposable()

    private val _events = MutableLiveData<Event<FavoriteListNavigation>>()
    val events: LiveData<Event<FavoriteListNavigation>> get() = _events

    private val _favoriteList: LiveData<List<Character>>
        get() = LiveDataReactiveStreams.fromPublisher(
            getAllFavoriteCharacters.invoke()
        )
    val favoriteList: LiveData<List<Character>>
        get() = _favoriteList

    fun onFavoriteList(list: List<Character>) {
        if(list.isEmpty()){
            _events.value = Event(FavoriteListNavigation.ShowEmptyListMessage)
            return
        }

        _events.value = Event(FavoriteListNavigation.ShowCharacterList(list))
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    sealed class FavoriteListNavigation {
        data class ShowCharacterList(val characterList: List<Character>): FavoriteListNavigation()
        object ShowEmptyListMessage: FavoriteListNavigation()
    }
}