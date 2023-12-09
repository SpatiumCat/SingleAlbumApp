package ru.netology.singlealbumapp

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import ru.netology.singlealbumapp.adapter.OnInteractionListener
import ru.netology.singlealbumapp.adapter.TrackAdapter
import ru.netology.singlealbumapp.databinding.ActivityMainBinding
import ru.netology.singlealbumapp.dto.Track
import ru.netology.singlealbumapp.observer.MediaLifecycleObserver
import ru.netology.singlealbumapp.viewmodels.AlbumViewModel

class MainActivity : AppCompatActivity() {

    private val observer = MediaLifecycleObserver()
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AlbumViewModel

    private val completionListener = MediaPlayer.OnCompletionListener {
        val nextTrack = viewModel.data.value?.album?.tracks?.let { list ->
            var index = list.indexOf(
                viewModel.getPlayingTrack()?.copy(isNowPlaying = false)
            )
            if (index == list.lastIndex) list[0] else list[++index]
        } ?: return@OnCompletionListener
        firstStartMediaPlayer(observer, binding, nextTrack, viewModel)
    }

    private fun firstStartMediaPlayer(
        observer: MediaLifecycleObserver,
        binding: ActivityMainBinding,
        track: Track,
        viewModel: AlbumViewModel
    ) {
        viewModel.setAllTracksNotPlaying()
        observer.mediaPlayer?.reset()
        observer.mediaPlayer?.setDataSource(BuildConfig.BASE_URL + track.file)
            ?: return
        viewModel.setTrackPlaying(track)
        viewModel.isFirstTimePlayFlag = false
        observer.play()
        binding.playButton.isVisible = false
        binding.pauseButton.isVisible = true
    }

    private fun startMediaPlayerAfterPause(
        observer: MediaLifecycleObserver,
        binding: ActivityMainBinding,
        track: Track
    ) {
        observer.mediaPlayer?.start()
        binding.playButton.isVisible = false
        binding.pauseButton.isVisible = true
        viewModel.setTrackPlaying(track)
    }

    private fun pauseMediaPlayer(
        observer: MediaLifecycleObserver,
        binding: ActivityMainBinding,
        viewModel: AlbumViewModel
    ) {
        viewModel.setAllTracksNotPlaying()
        observer.pause()
        binding.playButton.isVisible = true
        binding.pauseButton.isVisible = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AlbumViewModel::class.java]
        lifecycle.addObserver(observer)
        observer.mediaPlayer?.setOnCompletionListener(completionListener)


        val adapter =
            TrackAdapter(viewModel.data.value?.album?.title ?: "", object : OnInteractionListener {
                override fun onPlay(track: Track) {
                    viewModel.getPlayingTrack()?.let { playingTrack ->
                        if (playingTrack.id == track.id) {
                            startMediaPlayerAfterPause(observer, binding, playingTrack)
                            return
                        }
                    }
                    firstStartMediaPlayer(observer, binding, track, viewModel)
                }

                override fun onPause() {
                    pauseMediaPlayer(observer, binding, viewModel)
                }
            })

        binding.trackList.adapter = adapter

        viewModel.data.observe(this) { state ->
            with(binding) {
                progressBar.isVisible = state.loading
                retryButton.isVisible = state.error
                emptyText.isVisible = state.empty
                albumName.text = state.album.title
                artistName.text = state.album.artist
                year.text = state.album.published
                genre.text = state.album.genre
            }
            adapter.albumName = state.album.title
            adapter.submitList(state.album.tracks)
        }

        binding.retryButton.setOnClickListener {
            viewModel.getAlbum()
        }

        binding.playButton.setOnClickListener {
            if (viewModel.isFirstTimePlayFlag) {
                val firstTrack =
                    viewModel.data.value?.album?.tracks?.get(0) ?: return@setOnClickListener
                viewModel.setAllTracksNotPlaying()
                firstStartMediaPlayer(observer, binding, firstTrack, viewModel)
            } else {
                viewModel.getPlayingTrack()?.let { playingTrack ->
                    startMediaPlayerAfterPause(observer, binding, playingTrack)
                }
            }
        }
        binding.pauseButton.setOnClickListener {
            pauseMediaPlayer(observer, binding, viewModel)
        }
    }
}