package com.example.androidstudio.bakingapp.utilities;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class PlayerUtils {


    public static SimpleExoPlayer initializePlayer(Context context, Uri mediaUri, Player.EventListener listener) {


        SimpleExoPlayer simpleExoPlayer;

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
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context);

        // Create a new ExoPlayer instance
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

        // Set the ExoPlayer.EventListener to this activity.
        simpleExoPlayer.addListener(listener);

        // Prepare the MediaSource.
        String userAgent = Util.getUserAgent(context, "BakingApp");
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaUri);

        // Attach the media source
        simpleExoPlayer.prepare(videoSource);

        // Set the player to begin playing
        simpleExoPlayer.setPlayWhenReady(true);

        return simpleExoPlayer;

    }






}
