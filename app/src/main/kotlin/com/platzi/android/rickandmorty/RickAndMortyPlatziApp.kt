package com.platzi.android.rickandmorty

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.platzi.android.rickandmorty.di.DaggerRickAndMortyComponent
import com.platzi.android.rickandmorty.di.RickAndMortyComponent

class RickAndMortyPlatziApp: Application() {

    //region Override Methods & Callbacks
    lateinit var component: RickAndMortyComponent

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        component = initAppComponent()
    }

    private fun initAppComponent() = DaggerRickAndMortyComponent.factory().create(this)

    //endregion

}
