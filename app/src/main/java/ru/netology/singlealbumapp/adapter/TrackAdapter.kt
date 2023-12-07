package ru.netology.singlealbumapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.singlealbumapp.databinding.CardSongBinding
import ru.netology.singlealbumapp.dto.Track

interface OnInteractionListener {
    fun onPlay(track: Track)
    fun onPause()
}

class TrackAdapter(
    var albumName: String,
    private val onInteractionListener: OnInteractionListener
): ListAdapter<Track, TrackViewHolder>(TrackDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = CardSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(albumName, track)
    }
}

class TrackViewHolder(
    private val binding: CardSongBinding,
    private val onInteractionListener: OnInteractionListener,
): RecyclerView.ViewHolder(binding.root) {

    fun bind(albumName: String, track: Track) {
        binding.apply {
            cardAlbumName.text = albumName
            cardSongName.text = track.file.split(".mp3")[0]
            cardPlayButton.isVisible = !track.isNowPlaying
            cardPauseButton.isVisible = track.isNowPlaying


            cardPlayButton.setOnClickListener{
                onInteractionListener.onPlay(track)
            }
            cardPauseButton.setOnClickListener {
                onInteractionListener.onPause()
            }
        }
    }
}

class TrackDiffCallback: DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return (oldItem.file == newItem.file) && (oldItem.isNowPlaying == newItem.isNowPlaying)
    }
}
