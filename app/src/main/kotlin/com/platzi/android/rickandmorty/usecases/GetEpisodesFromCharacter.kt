package com.platzi.android.rickandmorty.usecases

import com.platzi.android.rickandmorty.data.EpisodeRepository

class GetEpisodesFromCharacter(private val episodeRepository: EpisodeRepository) {
    fun invoke(episodeUrlList: List<String>) = episodeRepository.getEpisodesFromCharacter(episodeUrlList)
}