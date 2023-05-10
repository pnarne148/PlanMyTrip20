package com.example.planmytrip20

//import androidx.recyclerview.widget.RecyclerView
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.action.ViewActions.click
//import androidx.test.espresso.action.ViewActions.scrollTo
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.*
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
//import org.hamcrest.Matchers.not
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class HomepageInstrumentedTest {
//
//    @Test
//    fun recyclerViewVisible() {
//        onView(withId(R.id.recyclerViewCard)).check(matches(isDisplayed()))
//    }
//
//}

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.planmytrip20.ui.home.HomeFragment
import com.example.planmytrip20.ui.home.HomepageAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomepageInstrumentedTestCases {

    private lateinit var scenario: FragmentScenario<HomeFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer<HomeFragment>()
    }

    @Test
    fun recyclerView_DisplayedCorrectly() {
        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerViewCard)
            Espresso.onView(ViewMatchers.withId(R.id.recyclerViewCard))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.viewAllButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.viewAllButton))
                .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            assert(recyclerView.layoutManager is LinearLayoutManager)
        }
    }

    @Test
    fun clickOnItem_OpensDetailFragment() {
        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerViewCard)
            Espresso.onView(ViewMatchers.withId(R.id.recyclerViewCard))
                .perform(RecyclerViewActions.actionOnItemAtPosition<HomepageAdapter.ViewHolder>(0, ViewActions.click()))
            Espresso.onView(ViewMatchers.withId(R.id.nav_host_fragment_activity_main))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            assert(fragment.requireActivity().supportFragmentManager.backStackEntryCount == 1)
        }
    }

    @Test
    fun clickOnViewAllButton_OpensListFragment() {
        scenario.onFragment { fragment ->
            Espresso.onView(ViewMatchers.withId(R.id.viewAllButton))
                .perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withId(R.id.nav_host_fragment_activity_main))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            assert(fragment.requireActivity().supportFragmentManager.backStackEntryCount == 1)
        }
    }
}