package com.example.androidstudio.bakingapp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.utilities.RecipesBox;

import org.json.JSONException;
import org.json.JSONObject;



public class RecipesListAdapter extends RecyclerView.Adapter<RecipesListAdapter.RecipeViewHolder>{


    private static final String TAG = RecipesListAdapter.class.getSimpleName();

    // Store the count of items to be displayed in the recycler view
    private static int viewHolderCount;

    // Store the data to be displayed
    private RecipesBox recipesBox;

    /**
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    final private ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages and is implemented in MainActivity (communicates
     * with the MainActivity).
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, String recipeStringJSON);
    }

    /**
     * Constructor for RecipesListAdapter that accepts a number of items to display and the specification
     * for the ListItemClickListener.
     *
     * @param listener Listener for list item clicks
     */
    RecipesListAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
        viewHolderCount = 0;
    }


    void setRecipesData(RecipesBox recipesBox){
        this.recipesBox = recipesBox;
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
    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.recipe_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = (View) inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        RecipeViewHolder viewHolder = new RecipeViewHolder(view);

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
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {

        Log.d(TAG, "#" + position);

        JSONObject recipeJSON = recipesBox.getRecipeJSON(position);

        final String recipeName;

        try {

            recipeName = recipeJSON.getString("name");

            holder.recipeTextView.setText(recipeName);
            holder.recipeTextView.setVisibility(View.VISIBLE);

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

        if (null == recipesBox) return 0;
        return recipesBox.getNumberOfRecipes();

    }


    /**
     * Cache of the children views for a list item.
     */
    class RecipeViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView recipeImageView;
        TextView recipeTextView;
        Context context;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link RecipesListAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        private RecipeViewHolder(View itemView) {

            super(itemView);

            context = itemView.getContext();
            recipeImageView = (ImageView) itemView.findViewById(R.id.iv_main_recipe_image);
            recipeTextView = (TextView) itemView.findViewById(R.id.tv_main_recipe_image);

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

            JSONObject recipeJSON;
            recipeJSON = recipesBox.getRecipeJSON(clickedPosition);

            Log.v(TAG, "onClick recipeJSON:" + recipeJSON.toString());

            mOnClickListener.onListItemClick(clickedPosition, recipeJSON.toString());
        }

    }

}
