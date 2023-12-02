package ru.netology.singlealbumapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import ru.netology.singlealbumapp.dto.Album
import ru.netology.singlealbumapp.repository.AlbumRepository
import ru.netology.singlealbumapp.repository.AlbumRepositoryImpl
import java.io.IOException

class AlbumViewModel: ViewModel() {

    private val repository: AlbumRepository = AlbumRepositoryImpl()

    private var _album = MutableLiveData<Album>()
    val album: LiveData<Album>
        get() = _album

    init {
        getAlbum()
    }

    fun getAlbum () {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _album.postValue(repository.getAlbum())
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}