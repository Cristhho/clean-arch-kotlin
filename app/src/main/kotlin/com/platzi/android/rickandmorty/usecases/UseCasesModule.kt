package com.platzi.android.rickandmorty.usecases

import com.platzi.android.rickandmorty.data.CharacterRepository
import com.platzi.android.rickandmorty.data.EpisodeRepository
import dagger.Module
import dagger.Provides

@Module
class UseCasesModule {
    @Provides
    fun getAllCharactersProvider(characterRepository: CharacterRepository)
            = GetAllCharacters(characterRepository)

    @Provides
    fun getAllFavoriteCharactersProvider(characterRepository: CharacterRepository)
            = GetAllFavoriteCharacters(characterRepository)

    @Provides
    fun getEpisodesFromCharacterProvider(episodeRepository: EpisodeRepository)
            = GetEpisodesFromCharacter(episodeRepository)

    @Provides
    fun getFavoriteCharacterStatusProvider(characterRepository: CharacterRepository)
            = GetFavoriteCharacterStatus(characterRepository)

    @Provides
    fun updateFavoriteCharacterStatusProvider(characterRepository: CharacterRepository)
            = UpdateFavoriteCharacterStatus(characterRepository)
}