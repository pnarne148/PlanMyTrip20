//package com.example.planmytrip
//
//
//import androidx.test.espresso.Espresso
//import androidx.test.espresso.action.ViewActions
//import androidx.test.espresso.assertion.ViewAssertions
//import androidx.test.espresso.contrib.RecyclerViewActions
//import androidx.test.espresso.matcher.ViewMatchers
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.rule.ActivityTestRule
//import com.example.planmytrip20.MainActivity
//import com.example.planmytrip20.R
//import com.example.planmytrip20.ui.home.DetailFragment.ListAdapter
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class BucketListInstrumentTestCases {
//    @get:Rule
//    val activityTestRule = ActivityTestRule(MainActivity::class.java)
//
//    @Test
//    fun testRecyclerViewVisibility() {
//        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }
//
//    @Test
//    fun testItemClick() {
//        // Assuming there are at least 2 items in the list
//        val positionToClick = 1
//        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
//            .perform(RecyclerViewActions.actionOnItemAtPosition<ListAdapter.ViewHolder>(positionToClick, ViewActions.click()))
//
//        // Verify that the DetailFragment is displayed
//        Espresso.onView(ViewMatchers.withId(R.id.web_view))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }
//}
