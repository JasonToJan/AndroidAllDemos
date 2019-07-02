package com.devbrackets.android.exomediademo.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.devbrackets.android.exomedia.util.TimeFormatUtil;
import com.devbrackets.android.exomediademo.ExoManager;
import com.devbrackets.android.exomediademo.R;
import com.devbrackets.android.exomediademo.data.MediaItem;
import com.devbrackets.android.exomediademo.data.Samples;
import com.devbrackets.android.exomediademo.manager.PlaylistManager;
import com.devbrackets.android.playlistcore.data.MediaProgress;
import com.devbrackets.android.playlistcore.data.PlaybackState;
import com.devbrackets.android.playlistcore.data.PlaylistItemChange;
import com.devbrackets.android.playlistcore.listener.PlaylistListener;
import com.devbrackets.android.playlistcore.listener.ProgressListener;

import java.util.LinkedList;
import java.util.List;

/**
 * An example activity to show how to implement and audio UI
 * that interacts with the {@link com.devbrackets.android.playlistcore.service.BasePlaylistService}
 * and {@link com.devbrackets.android.playlistcore.manager.ListPlaylistManager}
 * classes.
 */
public class AudioPlayerActivity extends AppCompatActivity implements PlaylistListener<MediaItem>, ProgressListener {
    public static final String EXTRA_INDEX = "EXTRA_INDEX";
    public static final int PLAYLIST_ID = 4; //Arbitrary, for the example

    private ProgressBar loadingBar;
    private ImageView artworkView;

    private TextView currentPositionView;
    private TextView durationView;

    private SeekBar seekBar;
    private boolean shouldSetDuration;
    private boolean userInteracting;

    private ImageButton previousButton;
    private ImageButton playPauseButton;
    private ImageButton nextButton;

    private PlaylistManager playlistManager;
    private int selectedPosition = 0;

    private RequestManager glide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_player_activity);

        retrieveExtras();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playlistManager = ExoManager.getInstance().getPlaylistManager();
        playlistManager.registerPlaylistListener(this);
        playlistManager.registerProgressListener(this);

        //Makes sure to retrieve the current playback information
        //可见的时候，需要更新当前歌曲信息
        updateCurrentPlaybackInformation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playlistManager.unRegisterPlaylistListener(this);
        playlistManager.unRegisterProgressListener(this);
    }

    /**
     * 当前播放的歌曲发生变化，比如点击了上一首，下一首
     * @param currentItem
     * @param hasNext
     * @param hasPrevious
     * @return
     */
    @Override
    public boolean onPlaylistItemChanged(@Nullable MediaItem currentItem, boolean hasNext, boolean hasPrevious) {
        shouldSetDuration = true;

        //Updates the button states
        nextButton.setEnabled(hasNext);//是否还有下一首
        previousButton.setEnabled(hasPrevious);//是否还有上一首

        //Loads the new image
        if (currentItem != null) {
            glide.load(currentItem.getArtworkUrl()).into(artworkView);
        }

        return true;
    }

    /**
     * 当前歌曲的状态发生变化，比如点击了播放或者暂停
     * @param playbackState
     * @return
     */
    @Override
    public boolean onPlaybackStateChanged(@NonNull PlaybackState playbackState) {
        switch (playbackState) {
            case STOPPED:
                finish();
                break;

            case RETRIEVING:
            case PREPARING:
            case SEEKING:
                restartLoading();
                break;

            case PLAYING:
                doneLoading(true);
                break;

            case PAUSED:
                doneLoading(false);
                break;

            default:
                break;
        }

        return true;
    }

    /**
     * 当前进度发生变化
     * @param progress
     * @return
     */
    @Override
    public boolean onProgressUpdated(@NonNull MediaProgress progress) {
        if (shouldSetDuration && progress.getDuration() > 0) {
            shouldSetDuration = false;
            setDuration(progress.getDuration());
        }

        if (!userInteracting) {
            seekBar.setSecondaryProgress((int) (progress.getDuration() * progress.getBufferPercentFloat()));
            seekBar.setProgress((int)progress.getPosition());
            currentPositionView.setText(TimeFormatUtil.formatMs(progress.getPosition()));
        }

        return true;
    }

    /**
     * 页面可见后，直接执行
     * 更新当前歌曲信息
     * Makes sure to update the UI to the current playback item.
     */
    private void updateCurrentPlaybackInformation() {
        //当前歌曲位置更新
        PlaylistItemChange<MediaItem> itemChange = playlistManager.getCurrentItemChange();
        if (itemChange != null) {
            onPlaylistItemChanged(itemChange.getCurrentItem(), itemChange.getHasNext(), itemChange.getHasPrevious());
        }

        //当前歌曲状态更新
        PlaybackState currentPlaybackState = playlistManager.getCurrentPlaybackState();
        if (currentPlaybackState != PlaybackState.STOPPED) {
            onPlaybackStateChanged(currentPlaybackState);
        }

        //当前歌曲进度更新
        MediaProgress mediaProgress = playlistManager.getCurrentProgress();
        if (mediaProgress != null) {
            onProgressUpdated(mediaProgress);
        }
    }

    /**
     * Retrieves the extra associated with the selected playlist index
     * so that we can start playing the correct item.
     */
    private void retrieveExtras() {
        Bundle extras = getIntent().getExtras();
        selectedPosition = extras.getInt(EXTRA_INDEX, 0);
    }

    /**
     * Performs the initialization of the views and any other
     * general setup
     */
    private void init() {
        retrieveViews();
        setupListeners();

        glide = Glide.with(this);

        boolean generatedPlaylist = setupPlaylistManager();
        startPlayback(generatedPlaylist);
    }


    /**
     * Called when we receive a notification that the current item is
     * done loading.  This will then update the view visibilities and
     * states accordingly.
     *
     * @param isPlaying True if the audio item is currently playing
     */
    private void doneLoading(boolean isPlaying) {
        loadCompleted();
        updatePlayPauseImage(isPlaying);
    }

    /**
     * Updates the Play/Pause image to represent the correct playback state
     *
     * @param isPlaying True if the audio item is currently playing
     */
    private void updatePlayPauseImage(boolean isPlaying) {
        int resId = isPlaying ? R.drawable.playlistcore_ic_pause_black : R.drawable.playlistcore_ic_play_arrow_black;
        playPauseButton.setImageResource(resId);
    }

    /**
     * Used to inform the controls to finalize their setup.  This
     * means replacing the loading animation with the PlayPause button
     */
    public void loadCompleted() {
        playPauseButton.setVisibility(View.VISIBLE);
        previousButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE );

        loadingBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Used to inform the controls to return to the loading stage.
     * This is the opposite of {@link #loadCompleted()}
     */
    public void restartLoading() {
        playPauseButton.setVisibility(View.INVISIBLE);
        previousButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE );

        loadingBar.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the {@link #seekBar}s max and updates the duration text
     *
     * @param duration The duration of the media item in milliseconds
     */
    private void setDuration(long duration) {
        seekBar.setMax((int)duration);
        durationView.setText(TimeFormatUtil.formatMs(duration));
    }

    /**
     * 构造一个播放列表
     * Retrieves the playlist instance and performs any generation
     * of content if it hasn't already been performed.
     *
     * @return True if the content was generated
     */
    private boolean setupPlaylistManager() {
        playlistManager = ExoManager.getInstance().getPlaylistManager();

        //There is nothing to do if the currently playing values are the same
        if (playlistManager.getId() == PLAYLIST_ID) {
            return false;//之前有了
        }

        List<MediaItem> mediaItems = new LinkedList<>();
        for (Samples.Sample sample : Samples.getAudioSamples()) {
            MediaItem mediaItem = new MediaItem(sample, true);
            mediaItems.add(mediaItem);
        }

        playlistManager.setParameters(mediaItems, selectedPosition);
        playlistManager.setId(PLAYLIST_ID);

        return true;
    }

    /**
     * findView
     * Populates the class variables with the views created from the
     * xml layout file.
     */
    private void retrieveViews() {
        loadingBar = findViewById(R.id.audio_player_loading);
        artworkView = findViewById(R.id.audio_player_image);

        currentPositionView = findViewById(R.id.audio_player_position);
        durationView = findViewById(R.id.audio_player_duration);

        seekBar = findViewById(R.id.audio_player_seek);

        previousButton = findViewById(R.id.audio_player_previous);
        playPauseButton = findViewById(R.id.audio_player_play_pause);
        nextButton = findViewById(R.id.audio_player_next);
    }

    /**
     * 设置按钮监听，上一首，下一首，播放暂停，进度条滑动
     * Links the SeekBarChanged to the {@link #seekBar} and
     * onClickListeners to the media buttons that call the appropriate
     * invoke methods in the {@link #playlistManager}
     */
    private void setupListeners() {
        seekBar.setOnSeekBarChangeListener(new SeekBarChanged());

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistManager.invokePrevious();
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistManager.invokePausePlay();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistManager.invokeNext();
            }
        });
    }

    /**
     * 自动播放，之前有播放则不必了
     * 重新生成一个队列，或者当前位置变化，才开始播放
     * Starts the audio playback if necessary.
     *
     * @param forceStart True if the audio should be started from the beginning even if it is currently playing
     */
    private void startPlayback(boolean forceStart) {
        //If we are changing audio files, or we haven't played before then start the playback
        if (forceStart || playlistManager.getCurrentPosition() != selectedPosition) {
            playlistManager.setCurrentPosition(selectedPosition);
            playlistManager.play(0, false);
        }
    }

    /**
     * Listens to the seek bar change events and correctly handles the changes
     */
    private class SeekBarChanged implements SeekBar.OnSeekBarChangeListener {
        private int seekPosition = -1;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }

            seekPosition = progress;
            currentPositionView.setText(TimeFormatUtil.formatMs(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            userInteracting = true;

            seekPosition = seekBar.getProgress();
            playlistManager.invokeSeekStarted();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            userInteracting = false;

            //noinspection Range - seekPosition won't be less than 0
            playlistManager.invokeSeekEnded(seekPosition);
            seekPosition = -1;
        }
    }
}
