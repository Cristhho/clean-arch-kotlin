package com.platzi.android.rickandmorty.usescases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.platzi.android.rickandmorty.data.CharacterRepository
import com.platzi.android.rickandmorty.domain.Character
import com.platzi.android.rickandmorty.domain.Location
import com.platzi.android.rickandmorty.domain.Origin
import com.platzi.android.rickandmorty.usecases.GetAllCharacters
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetAllCharactersTest {
    @Mock
    private lateinit var characterRepository: CharacterRepository

    private lateinit var getAllCharacters: GetAllCharacters

    @Before
    fun setUp() {
        getAllCharacters = GetAllCharacters(characterRepository)
    }

    @Test
    fun `get all characters should return a list of characters given a page number`() {
        val expectedResult = listOf(mockedCharacter.copy(id = 1))
        given(characterRepository.getAllCharacters(any())).willReturn(Single.just(expectedResult))

        getAllCharacters.invoke(1)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueCount(1)
            .assertValue(expectedResult)
    }
}

val mockedOrigin = Origin("", "")
val mockedLocation = Location("", "")
val mockedCharacter = Character(0, "", null, "", "", "", mockedOrigin, mockedLocation, listOf(""))