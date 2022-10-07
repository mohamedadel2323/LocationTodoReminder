package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var fakeDataSource: FakeDataSource

    private val testReminder1 =
        ReminderDataItem("testTitle1", "testDescription", "testLocation", 120.0, 60.0, "testId")
    private val testReminder2 =
        ReminderDataItem("", "testDescription", "testLocation", 120.0, 60.0, "testId")
    private val reminder2 =
        ReminderDTO("testTitle2", "testDescription", "testLocation", 120.0, 60.0, "testId")
    private val reminder3 =
        ReminderDTO("testTitle3", "testDescription", "testLocation", 120.0, 60.0, "testId")
    private var poi = PointOfInterest(LatLng(120.0, 60.0), "place id", "place name")

    @Before
    fun init() = mainCoroutineRule.runBlockingTest {
        fakeDataSource = FakeDataSource()

        fakeDataSource.saveReminder(reminder2)
        fakeDataSource.saveReminder(reminder3)
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun release() {
        stopKoin()
    }


    @Test
    fun validateReminder_successValue_returnTrue() {
        // Given - reminder object with "completed data" passed to validateEnteredDate function
        val validationState = saveReminderViewModel.validateEnteredData(testReminder1)

        //Then - Check that the return is true
        assertThat(validationState, `is`(true))
    }

    //ErrorTesting
    @Test
    fun validateReminder_errorNullValue_returnTrue() {
        // Given - reminder object with "empty title" passed to validateEnteredDate function
        val validationState = saveReminderViewModel.validateEnteredData(testReminder2)

        //Then - Check that the return is false and check that the error message has been set correctly
        assertThat(validationState, `is`(false))
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_enter_title)
        )
    }

    @Test
    fun setPointOfInterest_setSelectedPoiToLiveData() {
        //Given - set the point of interest
        saveReminderViewModel.setSelectedPoi(poi)

        //when - Getting the updated selectedPoi live data.
        val value = saveReminderViewModel.selectedPOI.getOrAwaitValue()

        //Then Check if its value is not null and check that it has been set correctly
        assertThat(value, not(nullValue()))
        assertThat(value.name, `is`("place name"))
    }

    @Test
    fun validateAndSaveReminder_loading() {

        //Given - load the reminders
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(testReminder1)

        //when - Getting the updated show loading live data before the reminder saved to the data source
        //Then Check if show loading live date value is true
        val showLoadingValueBefore = saveReminderViewModel.showLoading.getOrAwaitValue()
        assertThat(
            showLoadingValueBefore, `is`(true)
        )

        //when - Getting the updated show loading live data after the reminder saved to the data source
        //Then Check if show loading live date value is false
        mainCoroutineRule.resumeDispatcher()
        val showLoadingValueAfter = saveReminderViewModel.showLoading.getOrAwaitValue()
        assertThat(
            showLoadingValueAfter, `is`(false)
        )
    }


}