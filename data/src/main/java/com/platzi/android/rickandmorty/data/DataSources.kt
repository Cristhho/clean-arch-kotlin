package com.platzi.android.rickandmorty.data

import com.platzi.android.rickandmorty.domain.Character
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface RemoteCharacterDataSource {
    fun getAllCharacters(page: Int): Single<List<Character>>
}

interface LocalCharacterDataSource {
    fun getAllFavoriteCharacters(): Flowable<List<Character>>
    fun getFavoriteCharacterStatus(id: Int): Maybe<Boolean>
    fun updateFavoriteStatus(character: Character): Maybe<Boolean>
}