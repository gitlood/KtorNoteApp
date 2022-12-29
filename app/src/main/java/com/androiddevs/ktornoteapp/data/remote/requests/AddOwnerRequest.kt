package com.androiddevs.ktornoteapp.data.remote.requests

data class AddOwnerRequest(
    val noteID: String,
    val owner: String
)