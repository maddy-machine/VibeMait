package com.runanywhere.startup_hackathon20

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.UUID
import java.util.concurrent.TimeUnit

class EventViewModel : ViewModel() {
    val events = mutableStateListOf<Event>()
    private val _guestsByEvent = mutableStateMapOf<String, MutableList<Guest>>()
    private val _expensesByEvent = mutableStateMapOf<String, MutableList<Expense>>()

    fun addEvent(event: Event) {
        val newEvent = event.copy(id = UUID.randomUUID().toString())
        events.add(newEvent)
        _guestsByEvent[newEvent.id] = mutableListOf()
        _expensesByEvent[newEvent.id] = mutableListOf()
    }

    fun getTasksForEvent(eventId: String): List<Task> {
        return emptyList() // Placeholder
    }

    fun toggleTask(eventId: String, taskId: String) {
        // Placeholder
    }

    fun getGuestsForEvent(eventId: String): List<Guest> {
        return _guestsByEvent[eventId] ?: emptyList()
    }

    fun addGuest(eventId: String, guest: Guest) {
        _guestsByEvent[eventId]?.add(guest)
    }

    fun updateGuestRsvp(eventId: String, guestId: String, newStatus: RSVPStatus) {
        _guestsByEvent[eventId]?.find { it.id == guestId }?.let {
            val updatedGuest = it.copy(rsvpStatus = newStatus)
            val guestList = _guestsByEvent[eventId]
            val guestIndex = guestList?.indexOf(it)
            if (guestIndex != null && guestIndex != -1) {
                guestList[guestIndex] = updatedGuest
            }
        }
    }

    fun getExpensesForEvent(eventId: String): List<Expense> {
        return _expensesByEvent[eventId] ?: emptyList()
    }

    fun addExpense(eventId: String, expense: Expense) {
        _expensesByEvent[eventId]?.add(expense)
    }

    fun scheduleReminder(context: Context, event: Event) {
        val workManager = WorkManager.getInstance(context)
        val inputData = Data.Builder()
            .putString("EVENT_NAME", event.name)
            .build()

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(inputData)
            .setInitialDelay(10, TimeUnit.SECONDS) // For testing, trigger after 10 seconds
            .build()

        workManager.enqueue(notificationWorkRequest)
    }
}
