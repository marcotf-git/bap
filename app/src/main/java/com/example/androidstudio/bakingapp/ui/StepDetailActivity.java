package com.example.androidstudio.bakingapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.androidstudio.bakingapp.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/***
 * This activity has the function of starting the step detail fragment
 */
public class StepDetailActivity extends AppCompatActivity
    implements Player.EventListener{

    private static final String TAG = StepDetailActivity.class.getSimpleName();

    // For the player
    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    // For the thumbnail
    private ImageView thumbnailView;

    private String videoURL;
    private String thumbnailURL;

    // Fields for handling the saving and restoring of view state
    private static final String EXOPLAYER_VIEW_STATE = "exoplayerViewState";
    private Parcelable exoplayerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra("thumbnailURL")) {
            thumbnailURL = intentThatStartedThisActivity.getStringExtra("thumbnailURL");
        }

        if (intentThatStartedThisActivity.hasExtra("videoURL")) {
            videoURL = intentThatStartedThisActivity.getStringExtra("videoURL");
        }

        // only create new fragment when there is no previously saved state
        if(savedInstanceState == null) {

            // Create a new StepDetailFragment instance and display it using the FragmentManager
            StepDetailFragment stepDetailFragment = new StepDetailFragment();

            // Set the fragment data
            if (intentThatStartedThisActivity.hasExtra("id")) {
                stepDetailFragment.setId(intentThatStartedThisActivity.getIntExtra("id", 0));
            } else {
                stepDetailFragment.setId(0);
            }

            if (intentThatStartedThisActivity.hasExtra("description")) {
                stepDetailFragment.setDescription(intentThatStartedThisActivity.getStringExtra("description"));
            } else {
                stepDetailFragment.setDescription("No step description available.");
            }


            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager fragmentManager = getSupportFragmentManager();

            // Fragment transaction
            fragmentManager.beginTransaction()
                    .add(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        }

        // Initialize the Media Session.
        initializeMediaSession();

        // Initialize the player view.
        mPlayerView = (PlayerView) findViewById(R.id.playerView);

        // Initialize the thumbnail view
        thumbnailView = (ImageView) findViewById(R.id.iv_thumbnail);


        // Load the video
        if (!videoURL.equals("")) {

            Log.v(TAG, "videoURL:" + videoURL);
            // Initialize the player.
            initializePlayer(Uri.parse(videoURL));

        } else {

            mPlayerView.setVisibility(View.GONE);

            // Show the thumbnail, if exists
            if (!thumbnailURL.equals("")) {

                Log.v(TAG, "thumbnailURL:" + thumbnailURL);
                /*
                 * Use the call back of picasso to manage the error in loading thumbnail.
                 */
                Picasso.with(this)
                        .load(thumbnailURL)
                        .into(thumbnailView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.v(TAG, "Thumbnail loaded");
                                thumbnailView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                Log.e(TAG, "Error in loading thumbnail");
                                thumbnailView.setVisibility(View.GONE);
                            }
                        });
            }
        }


    }


    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(this, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == Player.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == Player.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.e(TAG, "Error in ExoPlayer");
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }


    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {

        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.

            // Measures bandwidth during playback. Can be null if not required.
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            // Create a default TrackSelector
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            // Load Control
            LoadControl loadControl = new DefaultLoadControl();
            // Renderer
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);

            // Create a new ExoPlayer instance
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

            // Attach the player to the view
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "BakingApp");
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, userAgent);
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaUri);

            // Attach the media source
            mExoPlayer.prepare(videoSource);

            // Set the player to begin playing
            mExoPlayer.setPlayWhenReady(true);
        }

    }


    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }


    // Release the player when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mExoPlayer) {
            releasePlayer();
        }
    }

    // This method is saving the position of the recycler view
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        Parcelable exoplayerViewState = mPlayerView.getMeasuredState();
//        savedInstanceState.putParcelable(EXOPLAYER_VIEW_STATE, recyclerViewState);
//        super.onSaveInstanceState(savedInstanceState);
//    }

    // This method is loading the saved position of the recycler view
    // There is also a call on the post execute method in the loader, for updating the view
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        exoplayerViewState = savedInstanceState.getParcelable(EXOPLAYER_VIEW_STATE);
//        mPlayerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
//    }

}
