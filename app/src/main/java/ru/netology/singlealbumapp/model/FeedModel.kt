package ru.netology.singlealbumapp.model

import ru.netology.singlealbumapp.dto.Album

data class FeedModel(
    val album: Album = Album(),
    val loading: Boolean = false,
    val empty: Boolean = false,
    val error: Boolean = false,
) {
}