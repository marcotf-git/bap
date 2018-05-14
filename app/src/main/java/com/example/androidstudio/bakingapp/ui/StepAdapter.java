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
import com.example.androidstudio.bakingapp.data.Step;

import java.util.ArrayList;


public class StepAdapter extends ArrayAdapter<Step> {


    StepAdapter(Context context, ArrayList<Step> ingredients){
        super(context, 0, ingredients);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.step_list_item, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        Step currentStep = getItem(position);

        TextView idView = listItemView.findViewById(R.id.tv_step_id);
        TextView shortDescriptionTextView = listItemView.findViewById(R.id.tv_step_short_description);

        if (null != currentStep) {

            idView.setText(String.valueOf(currentStep.getId()));
            shortDescriptionTextView.setText(currentStep.getShortDescription());

            Log.v("getView", "setting text:" + currentStep.getShortDescription());
        }

        return listItemView;
    }

}
