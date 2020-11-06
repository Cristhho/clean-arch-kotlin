package com.platzi.android.rickandmorty.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verify
import com.platzi.android.rickandmorty.domain.Character
import com.platzi.android.rickandmorty.domain.Location
import com.platzi.android.rickandmorty.domain.Origin
import com.platzi.android.rickandmorty.usecases.GetAllCharacters
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CharacterListViewModelTest {
    @get:Rule
    val rxSchedulerRule = RxSchedulerRules()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var getAllCharacters: GetAllCharacters

    @Mock
    lateinit var eventObserver: Observer<Event<CharacterListViewModel.CharacterListNavigation>>

    private lateinit var characterListViewModel: CharacterListViewModel

    @Before
    fun setUp() {
        characterListViewModel = CharacterListViewModel(getAllCharacters)
    }

    @Test
    fun `onGetAllCharacters should return an expected success list of characters`() {
        val expectedResult = listOf(mockedCharacter.copy(id = 1))
        given(getAllCharacters.invoke(any())).willReturn(Single.just(expectedResult))
        characterListViewModel.events.observeForever(eventObserver)
        characterListViewModel.onGetAllCharacters()
        verify(eventObserver).onChanged(Event(
            CharacterListViewModel.CharacterListNavigation.ShowCharacterList(expectedResult)
        ))
    }
}

val mockedOrigin = Origin("", "")
val mockedLocation = Location("", "")
val mockedCharacter = Character(0, "", null, "", "", "", mockedOrigin, mockedLocation, listOf(""))