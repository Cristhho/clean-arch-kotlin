package com.platzi.android.rickandmorty.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.platzi.android.rickandmorty.domain.Character
import com.platzi.android.rickandmorty.domain.Episode
import com.platzi.android.rickandmorty.usecases.GetEpisodesFromCharacter
import com.platzi.android.rickandmorty.usecases.GetFavoriteCharacterStatus
import com.platzi.android.rickandmorty.usecases.UpdateFavoriteCharacterStatus
import io.reactivex.disposables.CompositeDisposable

class CharacterDetailViewModel(
    private val character: Character? = null,
    private val getFavoriteCharacterStatus: GetFavoriteCharacterStatus,
    private val updateFavoriteCharacterStatus: UpdateFavoriteCharacterStatus,
    private val getEpisodesFromCharacter: GetEpisodesFromCharacter): ViewModel() {

    private val disposable = CompositeDisposable()

    private val _characterValues = MutableLiveData<Character>()
    val characterValues: LiveData<Character> get() = _characterValues
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite
    private val _events = MutableLiveData<Event<CharacterDetailNavigation>>()
    val events: LiveData<Event<CharacterDetailNavigation>> get() = _events

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun onCharacterValidation() {
        if(character == null) {
            _events.value = Event(CharacterDetailNavigation.CloseActivity)
            return
        }

        _characterValues.value = character
        validateFavoriteCharacterStatus(character.id)
        requestShowEpisodeList(character.episodeList)
    }

    fun onUpdateFavoriteCharacterStatus() {
        disposable.add(
            updateFavoriteCharacterStatus.invoke(character!!)
                .subscribe { isFavorite ->
                    _isFavorite.value = isFavorite
                }
        )
    }

    private fun validateFavoriteCharacterStatus(characterId: Int){
        disposable.add(
            getFavoriteCharacterStatus.invoke(characterId)
                .subscribe { isFavorite ->
                    _isFavorite.value = isFavorite
                }
        )
    }

    private fun requestShowEpisodeList(episodeUrlList: List<String>){
        disposable.add(
            getEpisodesFromCharacter.invoke(episodeUrlList)
                .doOnSubscribe {
                    _events.value = Event(CharacterDetailNavigation.ShowEpisodeListLoading)
                }
                .subscribe(
                    { episodeList ->
                        _events.value = Event(CharacterDetailNavigation.HideEpisodeListLoading)
                        _events.value = Event(CharacterDetailNavigation.ShowEpisodeList(episodeList))
                    },
                    { error ->
                        _events.value = Event(CharacterDetailNavigation.HideEpisodeListLoading)
                        _events.value = Event(CharacterDetailNavigation.ShowEpisodeError(error))
                    })
        )
    }

    sealed class CharacterDetailNavigation {
        data class ShowEpisodeError(val error: Throwable) : CharacterDetailNavigation()
        data class ShowEpisodeList(val episodeList: List<Episode>) : CharacterDetailNavigation()
        object CloseActivity : CharacterDetailNavigation()
        object HideEpisodeListLoading : CharacterDetailNavigation()
        object ShowEpisodeListLoading : CharacterDetailNavigation()
    }
}