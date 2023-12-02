package ru.netology.singlealbumapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import ru.netology.singlealbumapp.adapter.OnInteractionListener
import ru.netology.singlealbumapp.adapter.TrackAdapter
import ru.netology.singlealbumapp.databinding.ActivityMainBinding
import ru.netology.singlealbumapp.viewmodels.AlbumViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = ViewModelProvider(this)[AlbumViewModel::class.java]
        val adapter = TrackAdapter (viewModel.album.value?.title ?: "", object : OnInteractionListener {
            override fun onPlay() {
            }

            override fun onPause() {
            }
        })
        binding.trackList.adapter = adapter

        viewModel.album.observe(this) {
            adapter.submitList(it.tracks)
        }

    }
}