package com.platzi.android.rickandmorty.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.platzi.android.rickandmorty.R
import com.platzi.android.rickandmorty.adapters.CharacterGridAdapter
import com.platzi.android.rickandmorty.requestmanager.APIConstants.BASE_API_URL
import com.platzi.android.rickandmorty.databinding.FragmentCharacterListBinding
import com.platzi.android.rickandmorty.di.CharacterListComponent
import com.platzi.android.rickandmorty.di.CharacterListModule
import com.platzi.android.rickandmorty.domain.Character
import com.platzi.android.rickandmorty.presentation.CharacterListViewModel
import com.platzi.android.rickandmorty.requestmanager.CharacterRequest
import com.platzi.android.rickandmorty.utils.app
import com.platzi.android.rickandmorty.utils.setItemDecorationSpacing
import com.platzi.android.rickandmorty.utils.showLongToast
import kotlinx.android.synthetic.main.fragment_character_list.*

class CharacterListFragment : Fragment() {

    //region Fields
    private lateinit var characterGridAdapter: CharacterGridAdapter
    private lateinit var listener: OnCharacterListFragmentListener
    private lateinit var characterRequest: CharacterRequest

    private lateinit var characterListComponent: CharacterListComponent

    private val characterListViewModel: CharacterListViewModel by lazy {
        characterListComponent.characterListViewModel
    }

    private val onScrollListener: RecyclerView.OnScrollListener by lazy {
        object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()

                characterListViewModel.onLoadMoreItems(visibleItemCount, firstVisibleItemPosition, totalItemCount)
            }
        }
    }

    //endregion

    //region Override Methods & Callbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        characterListComponent = context!!.app.component.inject(CharacterListModule())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            listener = context as OnCharacterListFragmentListener
        }catch (e: ClassCastException){
            throw ClassCastException("$context must implement OnCharacterListFragmentListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        characterRequest = CharacterRequest(BASE_API_URL)

        return DataBindingUtil.inflate<FragmentCharacterListBinding>(
            inflater,
            R.layout.fragment_character_list,
            container,
            false
        ).apply {
            lifecycleOwner = this@CharacterListFragment
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        characterGridAdapter = CharacterGridAdapter { character ->
            listener.openCharacterDetail(character)
        }
        characterGridAdapter.setHasStableIds(true)

        rvCharacterList.run{
            addOnScrollListener(onScrollListener)
            setItemDecorationSpacing(resources.getDimension(R.dimen.list_item_padding))

            adapter = characterGridAdapter
        }

        srwCharacterList.setOnRefreshListener {
            characterListViewModel.onRetryGetAllCharacter(rvCharacterList.adapter?.itemCount ?: 0)
        }

        characterListViewModel.events.observe(this, { events ->
            events?.getContentIfNotHandled()?.let { navigation ->
                when (navigation) {
                    is CharacterListViewModel.CharacterListNavigation.ShowCharacterError -> {
                        context?.showLongToast("Error")
                    }
                    is CharacterListViewModel.CharacterListNavigation.ShowCharacterList -> navigation.run {
                        characterGridAdapter.addData(characters)
                    }
                    CharacterListViewModel.CharacterListNavigation.HideLoading ->{
                        srwCharacterList.isRefreshing = false
                    }
                    CharacterListViewModel.CharacterListNavigation.ShowLoading ->{
                        srwCharacterList.isRefreshing = true
                    }
                }
            }
        })

        characterListViewModel.onGetAllCharacters()
    }

    //endregion

    //region Inner Classes & Interfaces

    interface OnCharacterListFragmentListener {
        fun openCharacterDetail(character: Character)
    }

    //endregion

    //region Companion object

    companion object {
        fun newInstance(args: Bundle? = Bundle()) = CharacterListFragment().apply {
            arguments = args
        }
    }

    //endregion
}
