package com.platzi.android.rickandmorty.di

import com.platzi.android.rickandmorty.domain.Character
import com.platzi.android.rickandmorty.presentation.CharacterDetailViewModel
import com.platzi.android.rickandmorty.usecases.GetEpisodesFromCharacter
import com.platzi.android.rickandmorty.usecases.GetFavoriteCharacterStatus
import com.platzi.android.rickandmorty.usecases.UpdateFavoriteCharacterStatus
import dagger.Module
import dagger.Provides
import dagger.Subcomponent

@Module
class CharacterDetailModule(private val character: Character?) {
    @Provides
    fun characterDetailViewModelProvider(getFavoriteCharacterStatus: GetFavoriteCharacterStatus,
        updateFavoriteCharacterStatus: UpdateFavoriteCharacterStatus,
        getEpisodesFromCharacter: GetEpisodesFromCharacter) = CharacterDetailViewModel(character,
        getFavoriteCharacterStatus, updateFavoriteCharacterStatus, getEpisodesFromCharacter)
}

@Subcomponent(modules = [(CharacterDetailModule::class)])
interface CharacterDetailComponent {
    val characterDetailViewModel: CharacterDetailViewModel
}