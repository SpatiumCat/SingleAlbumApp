package ru.netology.singlealbumapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycle.addObserver(observer)

        val viewModel = ViewModelProvider(this)[AlbumViewModel::class.java]
        val adapter =
            TrackAdapter(viewModel.data.value?.album?.title ?: "", object : OnInteractionListener {
                override fun onPlay(track: Track) {
                    observer.apply {
                        viewModel.setAllTracksNotPlaying()
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource(BuildConfig.BASE_URL + track.file)
                        viewModel.setTrackPlaying(track)
                        viewModel.isFirstTimePlayFlag = false
                        mediaPlayer?.setOnCompletionListener {
                            viewModel.setAllTracksNotPlaying()
                            val nextTrack = viewModel.data.value?.album?.tracks?.let { list ->
                                var index = list.indexOf(track)
                                list[++index]
                            } ?: return@setOnCompletionListener
                            it.setDataSource(BuildConfig.BASE_URL + nextTrack.file)
                            viewModel.setTrackPlaying(track)
                        }
                    }.play()
                }

                override fun onPause() {
                    observer.pause()
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
                pauseButton.isVisible = observer.mediaPlayer?.isPlaying ?: false
                playButton.isVisible = !observer.mediaPlayer?.isPlaying!! ?: true
            }
            adapter.albumName = state.album.title
            adapter.submitList(state.album.tracks)

        }
        observer.mediaPlayer?.isPlaying?.let {
            binding.playButton.isVisible = !it
            binding.pauseButton.isVisible = it
        }
        binding.retryButton.setOnClickListener {
            viewModel.getAlbum()
        }


        binding.playButton.setOnClickListener {
            if (viewModel.isFirstTimePlayFlag) {
                val firstTrack = viewModel.data.value?.album?.tracks?.get(0) ?: return@setOnClickListener
                viewModel.setAllTracksNotPlaying()
                observer.mediaPlayer?.setDataSource(BuildConfig.BASE_URL + firstTrack.file) ?: return@setOnClickListener
                viewModel.setTrackPlaying(firstTrack)
                viewModel.isFirstTimePlayFlag = false
                observer.play()
            } else {
                observer.play()
            }
        }
    }
}