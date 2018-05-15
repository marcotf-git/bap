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


public class IngredientsListAdapter extends RecyclerView.Adapter<IngredientsListAdapter.IngredientViewHolder> {


    private static final String TAG = IngredientsListAdapter.class.getSimpleName();

    // Store the count of items to be displayed in the recycler view
    private static int viewHolderCount;

    // Store the data to be displayed
    private JSONArray ingredientsArray;

    /**
     * Constructor for IngredientsListAdapter
     */
    IngredientsListAdapter() {

        viewHolderCount = 0;
    }


    void setIngredientsData(String ingredientsJSONString){

        try {
            ingredientsArray = new JSONArray(ingredientsJSONString);
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
    public IngredientsListAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.ingredient_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        IngredientsListAdapter.IngredientViewHolder viewHolder = new IngredientsListAdapter.IngredientViewHolder(view);

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
    public void onBindViewHolder(@NonNull final IngredientsListAdapter.IngredientViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder #" + position);

        Double quantity;
        String measure;
        String ingredientName;

        try {

            JSONObject ingredient = ingredientsArray.getJSONObject(position);
            quantity = ingredient.getDouble("quantity");
            measure = ingredient.getString("measure");
            ingredientName = ingredient.getString("ingredient");

            holder.quantityTextView.setText(quantity.toString());
            holder.measureTextView.setText(measure);
            holder.ingredientNameTextView.setText(ingredientName);

            Log.d(TAG, "onBindViewHolder ingredientName:" + ingredientName);

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

        if (null == ingredientsArray) return 0;
        return ingredientsArray.length();

    }


    /**
     * Cache of the children views for a list item.
     */
    class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_ingredient_quantity) TextView quantityTextView;
        @BindView(R.id.tv_ingredient_measure) TextView measureTextView;
        @BindView(R.id.tv_ingredient_name)  TextView ingredientNameTextView;
        final Context context;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link RecipesListAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        private IngredientViewHolder(View itemView) {

            super(itemView);

            context = itemView.getContext();

            ButterKnife.bind(this, itemView);

        }

    }

}
