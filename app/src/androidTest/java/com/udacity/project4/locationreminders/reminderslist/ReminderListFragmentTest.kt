package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.FakeDataSource
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {
    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupTest() {
        dataSource = FakeDataSource()
        viewModel =
                RemindersListViewModel(getApplicationContext(), dataSource)
        stopKoin()
        val myModule = module {
            single {
                viewModel
            }
        }
        startKoin {
            modules(listOf(myModule))
        }
    }

    //    TODO: test the navigation of the fragments.
    @Test
    fun navigate_add_reminderTest() = runBlockingTest {
        val fragmentScenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        fragmentScenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }
//    TODO: test the displayed data on the UI.

    @Test
    fun displayData_uiTest() = runBlockingTest {
        var reminder1 = ReminderDTO("title1", "description1", "dhaka1", 37.819927, -122.478256)
        /*   var reminder2 = ReminderDTO("title2", "description2", "dhaka2", 37.819927, -122.478256)
           var reminder3 = ReminderDTO("title3", "description3", "dhaka3", 37.819927, -122.478256)
   */
        dataSource.saveReminder(reminder1)
        launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        onView(withId(R.id.noDataTextView)).check(matches(CoreMatchers.not(isDisplayed())))
        onView(ViewMatchers.withText(reminder1.title)).check(matches(isDisplayed()))
        onView(ViewMatchers.withText(reminder1.description)).check(matches(isDisplayed()))
        onView(ViewMatchers.withText(reminder1.location)).check(matches(isDisplayed()))

    }
//    TODO: add testing for the error messages.

    @Test
    fun displayed_noDataTest() = runBlockingTest {

        dataSource.deleteAllReminders()


        launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }


    @After
    fun stopDown() {
        stopKoin()
    }
}