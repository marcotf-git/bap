package com.example.androidstudio.bakingapp.ui;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.data.Step;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * This test will check if the step description of the first recipe is shown.
 *
 * This test uses idling resources.
 */

@RunWith(AndroidJUnit4.class)
public class StepDetailActivityScreenTest {

    public static final int ITEM = 0;
    public static final String RECIPE_NAME = "Nutella Pie";
    public static final String FIRST_STEP = "Recipe Introduction";
    public static final String FIRST_STEP_DESCRIPTION = "Recipe Introduction";

    private IdlingResource mIdlingResource;


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);


    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(mIdlingResource);

    }

    /**
     * Go to the first recipe detail screen and check if the first ingredient is shown.
     */
    @Test
    public void testIfFirstStepDetailOfFirstRecipeIsShown() {

        onView(ViewMatchers.withId(R.id.rv_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(ITEM, click()));

        onView(withText(RECIPE_NAME)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.steps_list))
                .perform(ViewActions.scrollTo());

        onData(stepWithDescription(FIRST_STEP))
                .inAdapterView(withId(R.id.steps_list))
                .perform(click());

        onView(ViewMatchers.withId(R.id.tv_step_description))
                .check(matches(withText(FIRST_STEP_DESCRIPTION)));

    }


    // Unregister resources when not needed to avoid malfunction.
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

    // Matcher for the step
    public static Matcher<Object> stepWithDescription(final String description) {
        return new BoundedMatcher<Object, Step>(Step.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has description " + description);
            }
            @Override
            public boolean matchesSafely(Step step) {
                return step.getShortDescription().equals(description);
            }
        };
    }

}
