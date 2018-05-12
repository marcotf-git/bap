package com.example.androidstudio.bakingapp.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.data.Ingredient;

import java.util.ArrayList;


public class IngredientAdapter extends ArrayAdapter<Ingredient> {


    IngredientAdapter(Context context, ArrayList<Ingredient> ingredients){
        super(context, 0, ingredients);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.ingredient_list_item, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        Ingredient currentIngredient = getItem(position);

        TextView quantityView = (TextView) listItemView.findViewById(R.id.tv_ingredient_quantity);
        TextView measureTextView = (TextView) listItemView.findViewById(R.id.tv_ingredient_measure);
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.tv_ingredient_name);

        if (null != currentIngredient) {

            quantityView.setText(String.valueOf(currentIngredient.getIngredientQuantity()));
            measureTextView.setText(currentIngredient.getIngredientMeasure());
            nameTextView.setText(currentIngredient.getIngredient());

            Log.v("getView", "setting text:" + currentIngredient.getIngredient());
        }

        return listItemView;
    }

}
