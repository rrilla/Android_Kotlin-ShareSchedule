package com.example.share_schedule.data.remote.model.map

data class ResponseModel(
    val html_attributions: List<String>,
    val results: List<Place>,
    val next_page_token: String
)
