package ru.netology.singlealbumapp.repository

import ru.netology.singlealbumapp.dto.Album

interface AlbumRepository {
    suspend fun getAlbum(): Album
}