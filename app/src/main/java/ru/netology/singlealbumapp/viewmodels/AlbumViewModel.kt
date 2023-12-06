package ru.netology.singlealbumapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.singlealbumapp.dto.Track
import ru.netology.singlealbumapp.model.FeedModel
import ru.netology.singlealbumapp.repository.AlbumRepository
import ru.netology.singlealbumapp.repository.AlbumRepositoryImpl
import java.io.IOException

class AlbumViewModel: ViewModel() {

    private val repository: AlbumRepository = AlbumRepositoryImpl()

    private var _data = MutableLiveData<FeedModel>()
    val data: LiveData<FeedModel>
        get() = _data

    var isFirstTimePlayFlag: Boolean = true
        init {
        getAlbum()
    }

    fun getAlbum () {
        _data.value = FeedModel(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val album = repository.getAlbum()
                _data.postValue(FeedModel(album = album, empty = album.tracks.isEmpty()))
            } catch (e: IOException) {
                _data.postValue(FeedModel(error = true))
                e.printStackTrace()
            } catch (e: Exception) {
                _data.postValue(FeedModel(error = true))
                e.printStackTrace()
            }
        }
    }

    fun setTrackPlaying (track: Track) {
        val model = _data.value ?: return
        _data.value = model.copy(
            album = model.album.copy(
                tracks = model.album.tracks.map { playingTrack ->
                    if (playingTrack.id == track.id) playingTrack.copy(isNowPlaying = true) else playingTrack
                }
            )
        )
    }

    fun setTrackNotPlaying (track: Track) {
        val model = _data.value ?: return
        _data.value = model.copy(
            album = model.album.copy(
                tracks = model.album.tracks.map { playingTrack ->
                    if (playingTrack.id == track.id) playingTrack.copy(isNowPlaying = false) else playingTrack
                }
            )
        )
    }
    fun setAllTracksNotPlaying() {
        val model = _data.value ?: return
        _data.value = model.copy(
            album = model.album.copy(
                tracks = model.album.tracks.map { playingTrack ->
                    playingTrack.copy(isNowPlaying = false)
                }
            )
        )
    }



}