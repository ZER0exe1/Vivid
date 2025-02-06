package dev.brahmkshatriya.echo.playback

import androidx.annotation.OptIn
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dev.brahmkshatriya.echo.playback.MediaItemUtils.track

@Suppress("unused")
@OptIn(UnstableApi::class)
// I couldn't make it work properly
// The only problem is when stuff is added while the shuffle is going, it fucks up everything
// aka, when shuffle is turned on with auto start radio, it fucks up everything
// feel free to make a pr to fix this
class ShufflePlayer(
    val player: Player,
) : ForwardingPlayer(player) {

    private fun getQueue() = (0 until mediaItemCount).map { player.getMediaItemAt(it) }

    private var isShuffled = false
    private var original = getQueue()

    override fun getShuffleModeEnabled() = isShuffled
    override fun setShuffleModeEnabled(enabled: Boolean) {
        if (enabled) original = getQueue()
        isShuffled = enabled
        changeQueue(if (enabled) original.shuffled() else original)
//        player.shuffleModeEnabled = enabled
    }

    override fun hasNextMediaItem(): Boolean {
        return currentMediaItemIndex < mediaItemCount - 1
    }

    private fun print(bruh:String) {
        println(bruh)
        println("$isShuffled list ${original.size}: ${original.map { it.track.title }}")
        println("player ${mediaItemCount}: ${getQueue().map { it.track.title }}")
    }

    private fun changeQueue(list: List<MediaItem>) {
    print("Change queue")
    if (list.size <= 1) return

    val currentMediaItem = player.currentMediaItem
    val currentIndex = list.indexOf(currentMediaItem)

    // Remove current media item from the player if it exists
    if (currentIndex != -1) {
        player.removeMediaItems(0, mediaItemCount - 1)
        player.addMediaItems(0, list)
        player.seekTo(currentIndex)
    } else {
        player.setMediaItems(list)
    }
}

override fun addMediaItem(mediaItem: MediaItem) {
    if (isShuffled) {
        original = original + mediaItem
        changeQueue(original.shuffled())
    } else {
        player.addMediaItem(mediaItem)
    }
    print("Add media item")
}

override fun addMediaItems(mediaItems: MutableList<MediaItem>) {
    if (isShuffled) {
        original = original + mediaItems
        changeQueue(original.shuffled())
    } else {
        player.addMediaItems(mediaItems)
    }
    print("Add media items")
}

    override fun addMediaItem(index: Int, mediaItem: MediaItem) {
        if (isShuffled) original = original + mediaItem
        player.addMediaItem(index, mediaItem)
    }   else {
        player.addMediaItems(index, mediaItem)
    }
    print("Add media item at $index")
}

    override fun addMediaItems(index: Int, mediaItems: MutableList<MediaItem>) {
        if (isShuffled) original = original + mediaItems
        player.addMediaItems(index, mediaItems)
    }   else {
        player.addMediaItems(index, mediaItems)
    }
    print("Add media items at $index")
}

    override fun removeMediaItem(index: Int) {
        if (isShuffled) original = original - getMediaItemAt(index)
        player.removeMediaItem(index)
        print("Remove media item at $index")
    }

    override fun removeMediaItems(fromIndex: Int, toIndex: Int) {
        if (isShuffled) original =
            original - (fromIndex until toIndex).map { getMediaItemAt(it) }.toSet()
        player.removeMediaItems(fromIndex, toIndex)
        print("Remove media items from $fromIndex to $toIndex")
    }

    override fun replaceMediaItem(index: Int, mediaItem: MediaItem) {
        if (isShuffled) original = original - getMediaItemAt(index) + mediaItem
        player.replaceMediaItem(index, mediaItem)
        print("Replace media item at $index")
    }

    override fun replaceMediaItems(
        fromIndex: Int, toIndex: Int, mediaItems: MutableList<MediaItem>
    ) {
        if (isShuffled) original =
            original - (fromIndex until toIndex).map { getMediaItemAt(it) }.toSet() + mediaItems
        player.replaceMediaItems(fromIndex, toIndex, mediaItems)
        print("Replace media items from $fromIndex to $toIndex")
    }

    override fun setMediaItem(mediaItem: MediaItem) {
        if (isShuffled) original = listOf(mediaItem)
        player.setMediaItem(mediaItem)
        print("Set media item")
    }

    override fun setMediaItem(mediaItem: MediaItem, resetPosition: Boolean) {
        if (isShuffled) original = listOf(mediaItem)
        player.setMediaItem(mediaItem, resetPosition)
        print("Set media item")
    }

    override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) {
        if (isShuffled) original = listOf(mediaItem)
        player.setMediaItem(mediaItem, startPositionMs)
        print("Set media item")
    }

    override fun setMediaItems(mediaItems: MutableList<MediaItem>) {
        if (isShuffled) original = mediaItems
        player.setMediaItems(mediaItems)
        print("Set media items")
    }

    override fun setMediaItems(mediaItems: MutableList<MediaItem>, resetPosition: Boolean) {
        if (isShuffled) original = mediaItems
        player.setMediaItems(mediaItems, resetPosition)
        print("Set media items")
    }

    override fun setMediaItems(
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ) {
        if (isShuffled) original = mediaItems
        player.setMediaItems(mediaItems, startIndex, startPositionMs)
        print("Set media items")
    }

    override fun clearMediaItems() {
        original = emptyList()
        player.clearMediaItems()
        print("Clear media items")
    }
}
