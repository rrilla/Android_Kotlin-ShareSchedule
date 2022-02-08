package com.example.share_schedule

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(
    private val context: Context
) {

    companion object {
        const val PREFERENCES_NAME = "shareSchedule"

        const val KEY_ID_TOKEN = "ID_TOKEN"
        const val KEY_CALENDAR_NEXTSYNC_TOKEN = "CALENDAR_NEXTSYNC_TOKEN"
        const val KEY_EVENT_NEXTSYNC_TOKEN = "EVENT_NEXTSYNC_TOKEN"
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    private val prefs by lazy { getPreferences(context) }

    private val editor by lazy { prefs.edit() }

    fun clear() {
        editor.clear()
        editor.apply()
    }

    fun putIdToken(idToken: String) {
        editor.putString(KEY_ID_TOKEN, idToken)
        editor.apply()
    }

    fun getIdToken(): String? {
        return prefs.getString(KEY_ID_TOKEN, null)
    }

    fun removedIdToken() {
        editor.putString(KEY_ID_TOKEN, null)
        editor.apply()
    }

    fun putCalendarNextSyncToken(syncToken: String) {
        editor.putString(KEY_CALENDAR_NEXTSYNC_TOKEN, syncToken)
        editor.apply()
    }

    fun getCalendarNextSyncToken(): String? {
        return prefs.getString(KEY_CALENDAR_NEXTSYNC_TOKEN, null)
    }

    fun removeCalendarNextSyncToken() {
        editor.putString(KEY_CALENDAR_NEXTSYNC_TOKEN, null)
        editor.apply()
    }

    fun putEventNextSyncToken(syncToken: String) {
        editor.putString(KEY_EVENT_NEXTSYNC_TOKEN, syncToken)
        editor.apply()
    }

    fun getEventNextSyncToken(): String? {
        return prefs.getString(KEY_EVENT_NEXTSYNC_TOKEN, null)
    }

    fun removeEventNextSyncToken() {
        editor.putString(KEY_EVENT_NEXTSYNC_TOKEN, null)
        editor.apply()
    }
}