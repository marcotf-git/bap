package com.example.androidstudio.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.utilities.Ingredient;
import com.example.androidstudio.bakingapp.utilities.Step;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity
    implements StepsFragment.OnItemClickListener,
    Player.EventListener{

    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    // The views variables
    private TextView mDisplayName;

    // The data of the recipe being viewed
    private String recipeStringJSON;
    private String recipeName = "";

    // The step being viewed
    private int mStep;

    // The array for storing information about the ingredients
    private final ArrayList<Ingredient> ingredients = new ArrayList<>();

    // The array for storing information about the steps
    private final ArrayList<Step> steps = new ArrayList<>();

        // A single-pane display refers to phone screens, and two-pane to tablet screens
    private boolean mTwopane;

    // For the player
    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    // For the thumbnail
    private ImageView thumbnailView;

    // Final Strings to store state information
    public static final String STEP_NUMBER = "step";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Determine if you are creating a two-pane or single-pane display
        if(findViewById(R.id.view_tablet_linear_layout) != null) {
            // this LinearLayout will only initially exists in the two-pane tablet case
            mTwopane = true;
        } else {
            mTwopane = false;
        }

        mDisplayName = (TextView) findViewById(R.id.tv_recipe_name);

        if (mTwopane) {



            // Initialize the player view.
            mPlayerView = (PlayerView) findViewById(R.id.playerView);

            // Initialize the thumbnail view
            thumbnailView = (ImageView) findViewById(R.id.iv_thumbnail);
        }

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra("recipeStringJSON")) {

            recipeStringJSON = intentThatStartedThisActivity.getStringExtra("recipeStringJSON");
            updateView(recipeStringJSON, savedInstanceState);

        }

    }


    public void updateView(String recipeStringJSON, Bundle savedState){

        try {

            // Convert the string to the JSON object
            JSONObject recipeJSON = new JSONObject(recipeStringJSON);

            // Extract the recipe name
            recipeName = recipeJSON.getString("name");

            // Update views
            mDisplayName.setText(recipeName);

            JSONArray ingredientsJSON = recipeJSON.getJSONArray("ingredients");
            updateIngredientsView(ingredientsJSON);

            JSONArray stepsJSON = recipeJSON.getJSONArray("steps");
            updateStepsView(stepsJSON, savedState);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * This helper function will load the IngredientsFragment, to show the ingredients in a list
     */
    public void updateIngredientsView (JSONArray ingredientsJSON) {

        Log.v(TAG, "updateIngredientsView ingredientsJSON:" + ingredientsJSON.toString());

        int nIngredients = ingredientsJSON.length();

        // Create an ArrayList with the ingredients for the recipe
        for (int i = 0; i < nIngredients; i++) {

            int ingredientQuantity;
            String ingredientMeasure;
            String ingredientName;

            JSONObject jsonObject;

            try {

                jsonObject = ingredientsJSON.getJSONObject(i);

                ingredientQuantity = jsonObject.getInt("quantity");
                ingredientMeasure = jsonObject.getString("measure");
                ingredientName = jsonObject.getString("ingredient");

                Ingredient ingredient = new Ingredient(ingredientQuantity, ingredientMeasure, ingredientName);

                ingredients.add(ingredient);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // At this point, we have an Array with the ingredients information
        Log.v(TAG, "updateIngredientsView ingredients:" + ingredients.toString());

        // Create a new IngredientsFragment instance and display it using the FragmentManager
        IngredientsFragment ingredientsFragment = new IngredientsFragment();

        // Set the fragment data
        ingredientsFragment.setIngredients(ingredients);

        // Use a FragmentManager and transaction to add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Fragment transaction
        fragmentManager.beginTransaction()
                .add(R.id.ingredients_container, ingredientsFragment)
                .commit();
    }


    /**
     * This helper function will load the StepsFragment, to show the steps in a list
     */
    public void updateStepsView (JSONArray stepsJSON, Bundle savedState) {

        Log.v(TAG, "updateStepsView stepsJSON:" + stepsJSON.toString());

        int nSteps = stepsJSON.length();

        // Create an ArrayList with the ingredients for the recipe
        for (int i = 0; i < nSteps; i++) {

            JSONObject jsonObject;

            try {

                int id;
                String shortDescription;
                String description;
                String videoURL;
                String thumbnailURL;

                jsonObject = stepsJSON.getJSONObject(i);

                id = jsonObject.getInt("id");
                shortDescription = jsonObject.getString("shortDescription");
                description = jsonObject.getString("description");
                videoURL = jsonObject.getString("videoURL");
                thumbnailURL = jsonObject.getString("thumbnailURL");

                Step step = new Step(id, shortDescription, description, videoURL, thumbnailURL);

                steps.add(step);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // At this point, we have an Array with the steps information
        Log.v(TAG, "updateStepsView steps:" + steps.toString());

        // Create a new StepsFragment instance and display it using the FragmentManager
        StepsFragment stepsFragment = new StepsFragment();

        // Set the fragment data
        stepsFragment.setSteps(steps);

        // Use a FragmentManager and transaction to add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Fragment transaction
        fragmentManager.beginTransaction()
                .add(R.id.steps_container, stepsFragment)
                .commit();


        Log.v(TAG, "updateStepsView savedState:" + savedState);

        if (savedState == null) {
            mStep = 0;
        } else {
            mStep = savedState.getInt(STEP_NUMBER);
        }

        Log.v(TAG, "updateStepsView mStep:" + mStep);

        // If two-pane screen, show also the step detail fragment with the initial step
        if (mTwopane && (savedState == null)) {

            // Create a new StepDetailFragment instance and display it using the FragmentManager
            StepDetailFragment stepDetailFragment = new StepDetailFragment();

            // Set the fragment data
            stepDetailFragment.setDescription(steps.get(mStep).getDescription());

            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager stepFragmentManager = getSupportFragmentManager();

            // Fragment transaction
            stepFragmentManager.beginTransaction()
                    .add(R.id.step_detail_container, stepDetailFragment)
                    .commit();

        }


        // If two pane, show also the first video and thumbnail for initial step
        if (mTwopane)  {

            // Initialize the Media Session.
            initializeMediaSession();

            String videoURL = steps.get(mStep).getVideoURL();
            String thumbnailURL = steps.get(mStep).getThumbnailURL();

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

    }


    /**
     * This is the listener that receives communication from the StepsFragment
     */
    @Override
    public void onStepSelected(int position) {

        //Toast.makeText(this, "Position= " + position, Toast.LENGTH_SHORT).show();
        Log.v(TAG, "onStepSelected:" + position);

        Step step = steps.get(position);

        mStep = position;

        Context context = RecipeDetailActivity.this;


        if (!mTwopane) {

            // If one-pane screen, call the StepDetailActivity to show the step detail

            Class destinationActivity = StepDetailActivity.class;
            Intent startChildActivityIntent = new Intent(context, destinationActivity);

            startChildActivityIntent.putExtra("id", step.getId());
            startChildActivityIntent.putExtra("shortDescription", step.getShortDescription());
            startChildActivityIntent.putExtra("description", step.getDescription());
            startChildActivityIntent.putExtra("videoURL", step.getVideoURL());
            startChildActivityIntent.putExtra("thumbnailURL", step.getThumbnailURL());

            startActivity(startChildActivityIntent);

        } else {

            // If two-pane screen, update the container with the step detail fragment

            // Create a new StepDetailFragment instance and display it using the FragmentManager
            StepDetailFragment stepDetailFragment = new StepDetailFragment();

            // Set the fragment data
            stepDetailFragment.setDescription(step.getDescription());

            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager fragmentManager = getSupportFragmentManager();

            // Fragment transaction
            fragmentManager.beginTransaction()
                    .replace(R.id.step_detail_container, stepDetailFragment)
                    .commit();


            // Also load the video or thumbnail

            String videoURL = step.getVideoURL();
            String thumbnailURL = step.getThumbnailURL();

            Log.v(TAG, "videoURL:" + videoURL);

            // First, release the player
            if (null != mExoPlayer) {
                releasePlayer();
            }

            // Load the video
            if (!videoURL.equals("")) {

                // Initialize the player.
                initializePlayer(Uri.parse(videoURL));

                mPlayerView.setVisibility(View.VISIBLE);

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
        mMediaSession.setCallback(new RecipeDetailActivity.MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

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


    // This method is saving the step being viewed
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STEP_NUMBER, mStep);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}
