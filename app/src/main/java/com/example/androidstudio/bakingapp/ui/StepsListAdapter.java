package com.example.androidstudio.bakingapp.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StepsListAdapter extends RecyclerView.Adapter<StepsListAdapter.StepViewHolder> {


    private static final String TAG = StepsListAdapter.class.getSimpleName();

    // Store the count of items to be displayed in the recycler view
    private static int viewHolderCount;

    // Store the data to be displayed
    private JSONArray stepsArray;
    private String stepsJSONString;

    /**
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    final private StepsListAdapter.ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages and is implemented in MainActivity (communicates
     * with the MainActivity).
     */
    public interface ListItemClickListener {
        void onListItemClick(
                int clickedPosition,
                int stepId,
                String shortDescription,
                String description,
                String videoURL,
                String thumbnailURL,
                String stepsJSONString);
    }

    /**
     * Constructor for StepsListAdapter that accepts a number of items to display and the specification
     * for the ListItemClickListener.
     *
     * @param listener Listener for list item clicks
     */
    StepsListAdapter(StepsListAdapter.ListItemClickListener listener) {
        mOnClickListener = listener;
        viewHolderCount = 0;
    }


    void setStepsData(String stepsJSONString){

        this.stepsJSONString = stepsJSONString;

        try {
            stepsArray = new JSONArray(stepsJSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    /**
     *
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public StepsListAdapter.StepViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.step_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        StepsListAdapter.StepViewHolder viewHolder = new StepsListAdapter.StepViewHolder(view);

        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: " + viewHolderCount);

        return viewHolder;
    }


    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull final StepsListAdapter.StepViewHolder holder, int position) {

        Log.d(TAG, "#" + position);

        Integer stepId;
        String shortDescription;

        try {
            JSONObject stepJSON = stepsArray.getJSONObject(position);
            stepId = stepJSON.getInt("id");
            shortDescription = stepJSON.getString("shortDescription");

            holder.idView.setText(stepId.toString());
            holder.shortDescriptionTextView.setText(shortDescription);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our recipes list
     */
    @Override
    public int getItemCount() {

        if (null == stepsArray) return 0;
        return stepsArray.length();

    }


    /**
     * Cache of the children views for a list item.
     */
    class StepViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.tv_step_id) TextView idView;
        @BindView(R.id.tv_step_short_description) TextView shortDescriptionTextView;

        final Context context;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link RecipesListAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        private StepViewHolder(View itemView) {

            super(itemView);

            context = itemView.getContext();

            ButterKnife.bind(this, itemView);

            // Call setOnClickListener on the View passed into the constructor (use 'this' as the OnClickListener)
            itemView.setOnClickListener(this);
        }

        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {

            int clickedPosition = getAdapterPosition();

            int stepId;
            String shortDescription;
            String description;
            String videoURL;
            String thumbnailURL;

            try {

                JSONObject stepJSON = stepsArray.getJSONObject(clickedPosition);

                stepId = stepJSON.getInt("id");
                shortDescription = stepJSON.getString("shortDescription");
                description = stepJSON.getString("description");
                videoURL = stepJSON.getString("videoURL");
                thumbnailURL = stepJSON.getString("thumbnailURL");

                Log.v(TAG, "onClick stepJSON:" + stepJSON.toString());

                // Calls the method implemented in the main activity
                mOnClickListener.onListItemClick(
                        clickedPosition,
                        stepId,
                        shortDescription,
                        description,
                        videoURL,
                        thumbnailURL,
                        stepsJSONString);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
