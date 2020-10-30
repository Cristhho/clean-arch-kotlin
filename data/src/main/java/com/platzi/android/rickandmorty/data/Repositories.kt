package com.platzi.android.rickandmorty.data

import com.platzi.android.rickandmorty.domain.Character

class CharacterRepository(
    private val remoteCharacterDataSource: RemoteCharacterDataSource,
    private val localCharacterDataSource: LocalCharacterDataSource
) {
    fun getAllCharacters(page: Int) = remoteCharacterDataSource.getAllCharacters(page)

    fun getAllFavoriteCharacters() = localCharacterDataSource.getAllFavoriteCharacters()
    fun getFavoriteCharacterStatus(id: Int) = localCharacterDataSource.getFavoriteCharacterStatus(id)
    fun updateFavoriteStatus(character: Character) = localCharacterDataSource.updateFavoriteStatus(character)
}

class EpisodeRepository(private val remoteEpisodesDataSource: RemoteEpisodesDataSource) {
    fun getEpisodesFromCharacter(episodeUrlList: List<String>) = remoteEpisodesDataSource.getEpisodesFromCharacter(episodeUrlList)
}