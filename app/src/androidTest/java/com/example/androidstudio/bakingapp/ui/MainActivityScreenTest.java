/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.androidstudio.bakingapp.ui;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.espresso.contrib.RecyclerViewActions;

import com.example.androidstudio.bakingapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;



/**
 * This test will check if the recipe title is being shown when the MainActivity is loaded.
 * Then, a user click on a GridView item in MainActivity which opens up the
 * corresponding RecipeDetailActivity, and it will verify if the title of the recipe is shown in the
 * RecipeDetailActivity screen.
 *
 * This test uses idling resources.
 */


@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    private static final int ITEM = 0;
    private static final String RECIPE_NAME = "Nutella Pie";

    private IdlingResource mIdlingResource;


    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);


    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(mIdlingResource);

    }


    /**
     * Scrolls to a Recipe and checks if its title is correct.
     */
    @Test
    public void scrollToRecipe_testRecipeName() {

        onView(ViewMatchers.withId(R.id.rv_recipes))
                .perform(RecyclerViewActions.scrollToPosition(ITEM))
                .check(matches(hasDescendant(withText(RECIPE_NAME))));

    }


    /**
     * Clicks on a Recipe and checks it opens up the RecipeDetailActivity with the correct recipe name.
     */
    @Test
    public void scrollToRecipe_clickAndTestIfOpenRecipeDetails() {

        onView(ViewMatchers.withId(R.id.rv_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(ITEM, click()));

        onView(withText(RECIPE_NAME)).check(matches(isDisplayed()));

    }


    // Unregister resources when not needed to avoid malfunction.
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

}