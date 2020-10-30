package com.platzi.android.rickandmorty.usecases

import com.platzi.android.rickandmorty.data.CharacterRepository
import com.platzi.android.rickandmorty.domain.Character

class UpdateFavoriteCharacterStatus(private val characterRepository: CharacterRepository) {
    fun invoke(character: Character) = characterRepository.updateFavoriteStatus(character)
}