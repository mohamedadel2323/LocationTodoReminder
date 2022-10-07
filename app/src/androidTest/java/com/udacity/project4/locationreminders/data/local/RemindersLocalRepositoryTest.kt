package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val reminder1 =
        ReminderDTO("testTitle1", "testDescription", "testLocation", 120.0, 60.0, "testId1")
    private val reminder2 =
        ReminderDTO("testTitle2", "testDescription", "testLocation", 120.0, 60.0, "testId2")
    private val reminder3 =
        ReminderDTO("testTitle3", "testDescription", "testLocation", 120.0, 60.0, "testId3")
    private val reminderList = listOf<ReminderDTO>(reminder1, reminder2).sortedBy { it.id }
    private val newReminders = listOf<ReminderDTO>(reminder3).sortedBy { it.id }

    private lateinit var reminderDao: RemindersDao
    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository


    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
        reminderDao = database.reminderDao()
        remindersLocalRepository = RemindersLocalRepository(reminderDao, Dispatchers.Unconfined)
    }

    @After
    fun release() {
        database.close()
    }

    @Test
    fun addAndGetAllReminders() = runBlockingTest {
        //Given - Database have some Reminders
        reminderDao.saveReminder(reminder1)
        reminderDao.saveReminder(reminder2)
        reminderDao.saveReminder(reminder3)

        //When - Getting reminders using repo
        val retrievedReminders = remindersLocalRepository.getReminders() as Success

        //Then - check the size of retrieved reminders list
        assertThat(retrievedReminders.data.size, `is`(3))
    }

    @Test
    fun saveSpecificReminderAndGetItById() = runBlockingTest {
        // save reminder using the repo
        remindersLocalRepository.saveReminder(reminder1)

        // Getting the reminder using repo
        val retrievedReminder = remindersLocalRepository.getReminder(reminder1.id) as Success

        // check it's the same saved reminder
        assertThat(retrievedReminder.data.id, `is`(reminder1.id))
        assertThat(retrievedReminder.data.title, `is`(reminder1.title))

        // check it's not the same saved reminder
        assertThat(retrievedReminder.data.title, `is`(not(reminder2.title)))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest {
        //Given - Database have some Reminders
        reminderDao.saveReminder(reminder1)
        reminderDao.saveReminder(reminder2)
        reminderDao.saveReminder(reminder3)
        // Check data existence
        var retrievedReminders = remindersLocalRepository.getReminders() as Success
        assertThat(retrievedReminders.data.size, `is`(3))


        // delete all reminders using repo
        remindersLocalRepository.deleteAllReminders()

        // check that the database is empty
        retrievedReminders = remindersLocalRepository.getReminders() as Success
        assertThat(retrievedReminders.data, `is`(emptyList()))
        assertThat(retrievedReminders.data.size, `is`(0))
    }


}