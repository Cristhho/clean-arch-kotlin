package com.platzi.android.rickandmorty.usecases

import com.platzi.android.rickandmorty.data.CharacterRepository

class GetAllCharacters(private val characterRepository: CharacterRepository) {
    fun invoke(currentPage: Int)  = characterRepository.getAllCharacters(currentPage)
}