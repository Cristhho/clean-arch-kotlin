package com.platzi.android.rickandmorty.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.platzi.android.rickandmorty.domain.Character
import com.platzi.android.rickandmorty.usecases.GetAllCharacters
import io.reactivex.disposables.CompositeDisposable

class CharacterListViewModel(private val getAllCharacters: GetAllCharacters): ViewModel() {
    private val disposable = CompositeDisposable()

    private val _events = MutableLiveData<Event<CharacterListNavigation>>()
    val events: LiveData<Event<CharacterListNavigation>> get() = _events

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun onLoadMoreItems(visibleItemCount: Int, firstVisibleItemPosition: Int, totalItemCount: Int) {
        if (isLoading || isLastPage || !isInFooter(visibleItemCount, firstVisibleItemPosition, totalItemCount)) {
            return
        }

        currentPage += 1
        onGetAllCharacters()
    }

    private fun isInFooter(
        visibleItemCount: Int,
        firstVisibleItemPosition: Int,
        totalItemCount: Int
    ): Boolean {
        return visibleItemCount + firstVisibleItemPosition >= totalItemCount
                && firstVisibleItemPosition >= 0
                && totalItemCount >= PAGE_SIZE
    }

    fun onRetryGetAllCharacter(itemCount: Int) {
        if (itemCount > 0) {
            _events.value = Event(CharacterListNavigation.HideLoading)
            return
        }

        onGetAllCharacters()
    }

    fun onGetAllCharacters(){
        disposable.add(
            getAllCharacters.invoke(currentPage)
                .doOnSubscribe {
                    _events.value = Event(CharacterListNavigation.ShowLoading)
                }
                .subscribe({ characterList ->
                    if (characterList.size < PAGE_SIZE) {
                        isLastPage = true
                    }

                    _events.value = Event(CharacterListNavigation.HideLoading)
                    _events.value = Event(CharacterListNavigation.ShowCharacterList(characterList))
                }, { error ->
                    isLastPage = true
                    _events.value = Event(CharacterListNavigation.HideLoading)
                    _events.value = Event(CharacterListNavigation.ShowCharacterError(error))
                })
        )
    }

    companion object {
        private const val PAGE_SIZE = 20
    }

    sealed class CharacterListNavigation {
        data class ShowCharacterError(val error: Throwable): CharacterListNavigation()
        data class ShowCharacterList(val characters: List<Character>): CharacterListNavigation()
        object HideLoading: CharacterListNavigation()
        object ShowLoading: CharacterListNavigation()
    }
}