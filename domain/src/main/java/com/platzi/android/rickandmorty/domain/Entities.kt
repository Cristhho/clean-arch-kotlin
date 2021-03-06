package com.platzi.android.rickandmorty.domain

data class Origin(
    val name: String,
    val url: String
)

data class Location(
    val name: String,
    val url: String
)

data class Character(
    val id: Int,
    val name: String,
    val image: String?,
    val gender: String,
    val specie: String,
    val status: String,
    val origin: Origin,
    val location: Location,
    val episodeList: List<String>
)

data class Episode (
    val id: Int,
    val name: String
)