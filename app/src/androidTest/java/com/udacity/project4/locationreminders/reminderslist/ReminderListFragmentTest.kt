package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.FakeDataSource
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.junit.Before
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
    private lateinit var remindersListViewModel: RemindersListViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
        stopKoin()
        val myModule = module {
            single {
                remindersListViewModel
            }
        }
        startKoin {
            modules(listOf(myModule))
        }
    }

    @Test
    fun clickOnAddReminderButton_navigateToAddReminderFragment() {

        // Given - Reminder list fragment is displayed with the fab
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }

        // When - performing a click to add reminder fab
        onView(withId(R.id.addReminderFAB)).perform(click())

        // Then - Check navigation to SaveReminderFragment called or not
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())

    }


    @Test
    fun testTheDataDisplayed() = runBlockingTest {

        // Given - on the reminder list screen with 3 reminders
        val reminder1 =
            ReminderDTO("testTitle1", "testDescription", "testLocation1", 120.0, 60.0, "testId1")
        val reminder2 =
            ReminderDTO("testTitle2", "testDescription", "testLocation2", 120.0, 60.0, "testId2")
        val reminder3 =
            ReminderDTO("testTitle3", "testDescription", "testLocation3", 120.0, 60.0, "testId3")

        dataSource.saveReminder(reminder1)
        dataSource.saveReminder(reminder2)
        dataSource.saveReminder(reminder3)

        // When - the fragment is opened
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // Then check for existence of the three reminders on the screen
        onView(withText(reminder1.title)).check(matches(isDisplayed()))
        onView(withText(reminder1.location)).check(matches(isDisplayed()))
        onView(withText(reminder2.title)).check(matches(isDisplayed()))
        onView(withText(reminder2.location)).check(matches(isDisplayed()))
        onView(withText(reminder3.title)).check(matches(isDisplayed()))
        onView(withText(reminder3.location)).check(matches(isDisplayed()))

    }

    @Test
    fun checkErrorMessage() = runBlockingTest {

        // Given - The data source is returning error as result when calling getReminders
        dataSource.setReturnError(true)
        dataSource.getReminders()

        // When - the fragment is opened
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        //Then - The snack bar is shown with the returned error message in the Error Result
        onView(withText("Test Error.")).check(matches(isDisplayed()))
    }
}