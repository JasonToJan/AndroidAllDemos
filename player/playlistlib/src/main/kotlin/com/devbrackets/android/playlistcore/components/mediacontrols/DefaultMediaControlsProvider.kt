/*
 * Copyright (C) 2016 - 2017 Brian Wernick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.devbrackets.android.playlistcore.components.mediacontrols

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.devbrackets.android.playlistcore.data.MediaInfo

open class DefaultMediaControlsProvider(protected val context: Context) : MediaControlsProvider {
    protected var enabled = true

    /**
     * Sets the volatile information for the remote views and controls.  This information is expected to
     * change frequently.
     *
     * @param title The title to display for the notification (e.g. A song name)
     * @param album The name of the album the media is found in
     * @param artist The name of the artist for the media item
     * @param notificationMediaState The current media state for the expanded (big) notification
     */
    override fun update(mediaInfo: MediaInfo, mediaSession: MediaSessionCompat) {
        //Updates the available playback controls
        val playbackStateBuilder = PlaybackStateCompat.Builder()
        playbackStateBuilder.setActions(getPlaybackOptions(mediaInfo.mediaState))
        playbackStateBuilder.setState(getPlaybackState(mediaInfo.mediaState.isPlaying), PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f)

        mediaSession.setPlaybackState(playbackStateBuilder.build())

        if (enabled && !mediaSession.isActive) {
            mediaSession.isActive = true
        }
    }

    @PlaybackStateCompat.State
    protected open fun getPlaybackState(isPlaying: Boolean): Int {
        return if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_CONNECTING
    }

    /**
     * Determines the available playback commands supported for the current media state
     *
     * @param mediaState The current media playback state
     * @return The available playback options
     */
    @PlaybackStateCompat.Actions
    protected open fun getPlaybackOptions(mediaState: MediaInfo.MediaState): Long {
        var availableActions = PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_PLAY_PAUSE

        if (mediaState.isNextEnabled) {
            availableActions = availableActions or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        }

        if (mediaState.isPreviousEnabled) {
            availableActions = availableActions or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        }

        return availableActions
    }
}