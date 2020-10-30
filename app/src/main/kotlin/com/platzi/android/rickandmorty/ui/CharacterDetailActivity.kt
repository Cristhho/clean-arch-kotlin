package com.platzi.android.rickandmorty.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.platzi.android.rickandmorty.R
import com.platzi.android.rickandmorty.adapters.EpisodeListAdapter
import com.platzi.android.rickandmorty.api.APIConstants.BASE_API_URL
import com.platzi.android.rickandmorty.api.CharacterRequest
import com.platzi.android.rickandmorty.api.CharacterRetrofitDataSource
import com.platzi.android.rickandmorty.api.EpisodeRequest
import com.platzi.android.rickandmorty.data.CharacterRepository
import com.platzi.android.rickandmorty.data.LocalCharacterDataSource
import com.platzi.android.rickandmorty.data.RemoteCharacterDataSource
import com.platzi.android.rickandmorty.database.CharacterDatabase
import com.platzi.android.rickandmorty.database.CharacterRoomDataSource
import com.platzi.android.rickandmorty.databinding.ActivityCharacterDetailBinding
import com.platzi.android.rickandmorty.domain.Character
import com.platzi.android.rickandmorty.parcelable.CharacterParcelable
import com.platzi.android.rickandmorty.parcelable.toCharacterDomain
import com.platzi.android.rickandmorty.presentation.CharacterDetailViewModel
import com.platzi.android.rickandmorty.presentation.Event
import com.platzi.android.rickandmorty.usecases.GetEpisodesFromCharacter
import com.platzi.android.rickandmorty.usecases.GetFavoriteCharacterStatus
import com.platzi.android.rickandmorty.usecases.UpdateFavoriteCharacterStatus
import com.platzi.android.rickandmorty.utils.Constants
import com.platzi.android.rickandmorty.utils.bindCircularImageUrl
import com.platzi.android.rickandmorty.utils.getViewModel
import com.platzi.android.rickandmorty.utils.showLongToast
import kotlinx.android.synthetic.main.activity_character_detail.*

class CharacterDetailActivity: AppCompatActivity() {

    //region Fields

    private lateinit var episodeListAdapter: EpisodeListAdapter
    private lateinit var binding: ActivityCharacterDetailBinding
    private val episodeRequest: EpisodeRequest by lazy {
        EpisodeRequest(BASE_API_URL)
    }

    private val getEpisodesFromCharacter: GetEpisodesFromCharacter by lazy {
        GetEpisodesFromCharacter(episodeRequest)
    }

    private val characterRequest: CharacterRequest by lazy { CharacterRequest(BASE_API_URL) }
    private val remoteCharacterDataSource: RemoteCharacterDataSource by lazy {
        CharacterRetrofitDataSource(characterRequest)
    }
    private val localCharacterDataSource: LocalCharacterDataSource by lazy {
        CharacterRoomDataSource(CharacterDatabase.getDatabase(applicationContext))
    }
    private val characterRepository: CharacterRepository by lazy {
        CharacterRepository(remoteCharacterDataSource, localCharacterDataSource)
    }
    private val getFavoriteCharacterStatus: GetFavoriteCharacterStatus by lazy {
        GetFavoriteCharacterStatus(characterRepository)
    }

    private val updateFavoriteCharacterStatus: UpdateFavoriteCharacterStatus by lazy {
        UpdateFavoriteCharacterStatus(characterRepository)
    }

    private val characterDetailViewModel: CharacterDetailViewModel by lazy {
        getViewModel {
            CharacterDetailViewModel(intent.getParcelableExtra<CharacterParcelable>(Constants.EXTRA_CHARACTER)?.toCharacterDomain(),
                getFavoriteCharacterStatus, updateFavoriteCharacterStatus, getEpisodesFromCharacter)
        }
    }

    //endregion

    //region Override Methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_character_detail)
        binding.lifecycleOwner = this@CharacterDetailActivity

        episodeListAdapter = EpisodeListAdapter { episode ->
            this@CharacterDetailActivity.showLongToast("Episode -> $episode")
        }
        rvEpisodeList.adapter = episodeListAdapter

        characterFavorite.setOnClickListener { characterDetailViewModel.onUpdateFavoriteCharacterStatus() }

        characterDetailViewModel.characterValues.observe(this, Observer(this::loadCharacter))
        characterDetailViewModel.isFavorite.observe(this, Observer(this::updateFavoriteIcon))
        characterDetailViewModel.events.observe(this, Observer(this::validateEvents))
        characterDetailViewModel.onCharacterValidation()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    //endregion

    //region Private Methods

    private fun loadCharacter(character: Character) {
        binding.characterImage.bindCircularImageUrl(
            url = character.image,
            placeholder = R.drawable.ic_camera_alt_black,
            errorPlaceholder = R.drawable.ic_broken_image_black
        )
        binding.characterDataName = character.name
        binding.characterDataStatus = character.status
        binding.characterDataSpecies = character.specie
        binding.characterDataGender = character.gender
        binding.characterDataOriginName = character.origin.name
        binding.characterDataLocationName = character.location.name
    }

    private fun updateFavoriteIcon(isFavorite: Boolean?){
        characterFavorite.setImageResource(
            if (isFavorite != null && isFavorite) {
                R.drawable.ic_favorite
            } else {
                R.drawable.ic_favorite_border
            }
        )
    }

    private fun validateEvents(event: Event<CharacterDetailViewModel.CharacterDetailNavigation>?) {
        event?.getContentIfNotHandled()?.let { navigation ->
            when (navigation) {
                is CharacterDetailViewModel.CharacterDetailNavigation.ShowEpisodeError -> navigation.run {
                    this@CharacterDetailActivity.showLongToast("Error -> ${error.message}")
                }
                is CharacterDetailViewModel.CharacterDetailNavigation.ShowEpisodeList -> navigation.run {
                    episodeListAdapter.updateData(episodeList)
                }
                CharacterDetailViewModel.CharacterDetailNavigation.CloseActivity -> {
                    this@CharacterDetailActivity.showLongToast(R.string.error_no_character_data)
                    finish()
                }
                CharacterDetailViewModel.CharacterDetailNavigation.HideEpisodeListLoading -> {
                    episodeProgressBar.isVisible = false
                }
                CharacterDetailViewModel.CharacterDetailNavigation.ShowEpisodeListLoading -> {
                    episodeProgressBar.isVisible = true
                }
            }
        }
    }

    //endregion
}
