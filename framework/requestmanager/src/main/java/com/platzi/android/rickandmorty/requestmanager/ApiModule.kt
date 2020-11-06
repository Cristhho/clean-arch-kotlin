package com.platzi.android.rickandmorty.requestmanager

import com.platzi.android.rickandmorty.data.RemoteCharacterDataSource
import com.platzi.android.rickandmorty.data.RemoteEpisodesDataSource
import com.platzi.android.rickandmorty.requestmanager.APIConstants.BASE_API_URL
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule {
    @Provides
    fun characterRequestProvider(@Named("baseUrl") baseUrl: String) = CharacterRequest(baseUrl)

    @Provides
    fun episodeRequestProvider(@Named("baseUrl") baseUrl: String) = EpisodeRequest(baseUrl)

    @Provides
    @Singleton
    @Named("baseUrl")
    fun baseUrlProvider(): String = BASE_API_URL

    @Provides
    fun remoteCharacterDataSourceProvider(characterRequest: CharacterRequest
        ): RemoteCharacterDataSource = CharacterRetrofitDataSource(characterRequest)

    @Provides
    fun remoteEpisodeDataSourceProvider(episodeRequest: EpisodeRequest
        ): RemoteEpisodesDataSource = EpisodeRetrofitDataSource(episodeRequest)
}