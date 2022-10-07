package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var reminderListViewModel: RemindersListViewModel
    private lateinit var emptyReminderListViewModel: RemindersListViewModel
    private lateinit var nullReminderListViewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource

    private val reminder2 =
        ReminderDTO("testTitle2", "testDescription", "testLocation", 120.0, 60.0, "testId")
    private val reminder3 =
        ReminderDTO("testTitle3", "testDescription", "testLocation", 120.0, 60.0, "testId2")

    @Before
    fun init() = mainCoroutineRule.runBlockingTest {
        fakeDataSource = FakeDataSource()

        fakeDataSource.saveReminder(reminder2)
        fakeDataSource.saveReminder(reminder3)
        reminderListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        emptyReminderListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource())
        nullReminderListViewModel =
            RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),
                FakeDataSource(null)
            )
    }

    @After
    fun release() {
        stopKoin()
    }


    @Test
    fun loadAllReminders_setRemindersLiveData() {
        //Given - load the reminders
        reminderListViewModel.loadReminders()

        //when - Getting the updated reminders list
        val value = reminderListViewModel.remindersList.getOrAwaitValue()

        //Then Check if it's not null and check equality of entered reminder and the live data reminder
        assertThat(value, not(nullValue()))
        assertThat(value.get(0).id, `is`(reminder2.id))
    }


    @Test
    fun loadAllReminders_loadingAppearanceTest() {
        //Given - load the reminders
        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()

        //when - Getting the updated show loading live data before the reminders list is updated
        //Then Check if show loading live date value is true
        val showLoadingValueBefore = reminderListViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoadingValueBefore, `is`(true))

        //when - Getting the updated show loading live data after the reminders list is updated
        //Then Check if show loading live date value is false
        mainCoroutineRule.resumeDispatcher()
        val showLoadingValueAfter = reminderListViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoadingValueAfter, `is`(false))
    }


    @Test
    fun validateReminder_errorEmptyValue_reminderListLiveDataIsEmpty() {
        //Given - load the reminders
        emptyReminderListViewModel.loadReminders()

        //when - Getting the reminders list live data when it empty
        val reminderList = emptyReminderListViewModel.remindersList.getOrAwaitValue()

        //Then Check if its empty or not
        assertThat(reminderList, `is`(emptyList()))
    }

    //ErrorTesting
    @Test
    fun loadAllReminders_errorNullValue_snackBarMessageUpdated() {
        //Given - load the reminders
        nullReminderListViewModel.loadReminders()

        //when - Getting the reminders list live data when it null
        val snackBarMessage = nullReminderListViewModel.showSnackBar.getOrAwaitValue()

        //Then Check if the right error message passed to the snack bar
        assertThat(snackBarMessage, `is`("Reminders not found."))
    }


}