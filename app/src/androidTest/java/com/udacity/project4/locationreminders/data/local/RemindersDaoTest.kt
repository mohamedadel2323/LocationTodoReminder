package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeBd() {
        database.close()
    }

    @Test
    fun insertAndGetReminders() = runBlockingTest {
        //Given - saving reminders to the database
        database.reminderDao().saveReminder(
            ReminderDTO(
                "testTitle1",
                "testDescription1",
                "testLocation1",
                120.0,
                60.0,
                "testId1"
            )
        )
        database.reminderDao().saveReminder(
            ReminderDTO(
                "testTitle2",
                "testDescription2",
                "testLocation2",
                120.0,
                60.0,
                "testId2"
            )
        )

        //when - Getting all reminders in the database
        val reminders = database.reminderDao().getReminders()

        //Then - Check the size of returned reminders is equal to the reminders i sent which is 2
        assertThat(reminders.size, `is`(2))
    }


    @Test
    fun insertAndGetReminderById() = runBlockingTest {
        //Given - saving a reminder to the database
        val reminder = ReminderDTO(
            "testTitle1",
            "testDescription1",
            "testLocation1",
            120.0,
            60.0,
            "testId1"
        )
        database.reminderDao().saveReminder(reminder)

        //when - Get the sent reminder by its id
        val resultReminder = database.reminderDao().getReminderById(reminder.id) as ReminderDTO

        //Then - Check that the sent reminder the same as retrieved reminder
        assertThat(resultReminder.id, `is`(reminder.id))
        assertThat(resultReminder.title, `is`(reminder.title))
        assertThat(resultReminder.description, `is`(reminder.description))
        assertThat(resultReminder.location, `is`(reminder.location))
        assertThat(resultReminder.latitude, `is`(reminder.latitude))
        assertThat(resultReminder.longitude, `is`(reminder.longitude))
    }


    @Test
    fun insertAndDeleteAllReminders() = runBlockingTest {
        //Given - insert reminders to the database
        database.reminderDao().saveReminder(
            ReminderDTO(
                "testTitle1",
                "testDescription1",
                "testLocation1",
                120.0,
                60.0,
                "testId1"
            )
        )
        database.reminderDao().saveReminder(
            ReminderDTO(
                "testTitle2",
                "testDescription2",
                "testLocation2",
                120.0,
                60.0,
                "testId2"
            )
        )

        //when - Deleting all reminders in the database and get all reminders
        database.reminderDao().deleteAllReminders()
        val reminders = database.reminderDao().getReminders()

        //Then - Check that the list of returned reminders is empty
        assertThat(reminders, `is`(emptyList()))
    }

}