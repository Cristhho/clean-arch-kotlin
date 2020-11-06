package com.platzi.android.rickandmorty.di

import com.platzi.android.rickandmorty.presentation.FavoriteListViewModel
import com.platzi.android.rickandmorty.usecases.GetAllFavoriteCharacters
import dagger.Module
import dagger.Provides
import dagger.Subcomponent

@Module
class FavoriteListModule {
    @Provides
    fun favoriteListViewModelProvider(getAllFavoriteCharacters: GetAllFavoriteCharacters)
            = FavoriteListViewModel(getAllFavoriteCharacters)
}

 @Subcomponent(modules = [(FavoriteListModule::class)])
interface FavoriteListComponent {
     val favoriteListViewModel: FavoriteListViewModel
 }