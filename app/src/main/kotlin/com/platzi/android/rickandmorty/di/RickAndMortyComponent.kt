package com.platzi.android.rickandmorty.di

import android.app.Application
import com.platzi.android.rickandmorty.data.RepositoryModule
import com.platzi.android.rickandmorty.databasemanager.DatabaseModule
import com.platzi.android.rickandmorty.requestmanager.ApiModule
import com.platzi.android.rickandmorty.usecases.UseCasesModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, DatabaseModule::class, RepositoryModule::class, UseCasesModule::class])
interface RickAndMortyComponent {
    fun inject(module: CharacterListModule): CharacterListComponent
    fun inject(module: FavoriteListModule): FavoriteListComponent
    fun inject(module: CharacterDetailModule): CharacterDetailComponent

    @Component.Factory
    interface Favorite {
        fun create(@BindsInstance app: Application): RickAndMortyComponent
    }
}