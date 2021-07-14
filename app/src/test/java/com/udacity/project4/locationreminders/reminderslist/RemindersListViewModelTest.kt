package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNot.not
import org.hamcrest.core.Is.`is`
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun initViewModel() {
        fakeDataSource = FakeDataSource()
        reminderListViewModel = RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),
                fakeDataSource
        )
    }

    @Test
    fun reminderListTest() {
        var reminder1 = ReminderDTO("title1", "description1", "dhaka1", 37.819927, -122.478256)
        var reminder2 = ReminderDTO("title2", "description2", "dhaka2", 37.819927, -122.478256)
        var reminder3 = ReminderDTO("title3", "description3", "dhaka3", 37.819927, -122.478256)
        val remindersList = mutableListOf(reminder1, reminder2, reminder3)
        fakeDataSource = FakeDataSource(remindersList!!)
        reminderListViewModel = RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),
                fakeDataSource
        )
        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue(), (not(emptyList())))
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue().size, `is`(remindersList.size))
    }

    @Test
    fun check_loading() {
        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun returnError() {
        fakeDataSource = FakeDataSource(null)
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        fakeDataSource.setReturnError(true)

        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.showSnackBar.getOrAwaitValue(), `is`("Location reminder data not found"))
    }

    @After
    fun stopDown() {
        stopKoin()
    }
}