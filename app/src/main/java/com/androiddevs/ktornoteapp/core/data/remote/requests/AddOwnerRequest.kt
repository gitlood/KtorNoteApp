package com.androiddevs.ktornoteapp.core.data.remote.requests

data class AddOwnerRequest(
    val owner: String,
    val noteID: String
)