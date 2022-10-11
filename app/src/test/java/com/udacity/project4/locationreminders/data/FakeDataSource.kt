package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success
import com.udacity.project4.locationreminders.data.dto.Result.Error

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = true
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        try {
            if (shouldReturnError){
                return Error("Test Error")
            }
            reminders?.let { return Success(ArrayList(it)) }
            return Error(" not found.")
        }catch (e : Exception){
            return Error(e.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        try {
            val result = reminders?.firstOrNull { it.id == id }
            result?.let {
                return Success(it)
            }
            return Error("Reminder Not Found")
        }catch (e : Exception){
            return Error(e.localizedMessage)
        }


    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }
}