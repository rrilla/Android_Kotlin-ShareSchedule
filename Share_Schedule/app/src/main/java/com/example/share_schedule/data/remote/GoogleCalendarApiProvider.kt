package com.example.share_schedule.data.remote

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.calendar.Calendar

object GoogleCalendarApiProvider {

    lateinit var apiService: Calendar

    fun createService(transport: HttpTransport, jsonFactory: JsonFactory, credential: GoogleAccountCredential) {
        apiService = Calendar.Builder(transport, jsonFactory, credential)
            .setApplicationName("Share Schedule")
            .build()
    }

}