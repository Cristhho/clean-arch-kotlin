package com.platzi.android.rickandmorty.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.platzi.android.rickandmorty.api.*
import com.platzi.android.rickandmorty.database.CharacterDao
import com.platzi.android.rickandmorty.database.CharacterEntity
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CharacterDetailViewModel(
    private val character: CharacterServer? = null,
    private val characterDao: CharacterDao,
    private val episodeRequest: EpisodeRequest): ViewModel() {

    private val disposable = CompositeDisposable()

    private val _characterValues = MutableLiveData<CharacterServer>()
    val characterValues: LiveData<CharacterServer> get() = _characterValues
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
        val characterEntity: CharacterEntity = character!!.toCharacterEntity()
        disposable.add(
            characterDao.getCharacterById(characterEntity.id)
                .isEmpty
                .flatMapMaybe { isEmpty ->
                    if(isEmpty){
                        characterDao.insertCharacter(characterEntity)
                    }else{
                        characterDao.deleteCharacter(characterEntity)
                    }
                    Maybe.just(isEmpty)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { isFavorite ->
                    _isFavorite.value = isFavorite
                }
        )
    }

    private fun validateFavoriteCharacterStatus(characterId: Int){
        disposable.add(
            characterDao.getCharacterById(characterId)
                .isEmpty
                .flatMapMaybe { isEmpty ->
                    Maybe.just(!isEmpty)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { isFavorite ->
                    _isFavorite.value = isFavorite
                }
        )
    }

    private fun requestShowEpisodeList(episodeUrlList: List<String>){
        disposable.add(
            Observable.fromIterable(episodeUrlList)
                .flatMap { episode: String ->
                    episodeRequest.baseUrl = episode
                    episodeRequest
                        .getService<EpisodeService>()
                        .getEpisode()
                        .toObservable()
                }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
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
        data class ShowEpisodeList(val episodeList: List<EpisodeServer>) : CharacterDetailNavigation()
        object CloseActivity : CharacterDetailNavigation()
        object HideEpisodeListLoading : CharacterDetailNavigation()
        object ShowEpisodeListLoading : CharacterDetailNavigation()
    }
}