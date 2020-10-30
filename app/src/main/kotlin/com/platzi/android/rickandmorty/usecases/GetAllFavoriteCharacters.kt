package com.platzi.android.rickandmorty.usecases

import com.platzi.android.rickandmorty.data.CharacterRepository

class GetAllFavoriteCharacters(private val characterRepository: CharacterRepository) {
    fun invoke() = characterRepository.getAllFavoriteCharacters()
}