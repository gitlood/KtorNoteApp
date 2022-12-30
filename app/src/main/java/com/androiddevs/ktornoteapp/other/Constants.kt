package com.androiddevs.ktornoteapp.other

object Constants {

    const val DATABASE_NAME = "notes_db"

    const val KEY_LOGGED_IN_EMAIL = "KEY_LOGGED_IN_EMAIL"
    const val KEY_LOGGED_IN_PASSWORD = "KEY_LOGGED_IN_PASSWORD"

    const val NO_EMAIL = "No Email"
    const val NO_PASSWORD = "No Password"

    const val DEFAULT_NOTE_COLOR = "FFA500"

    const val BASE_URL = "http://10.0.2.2:8080"

    const val ENCRYPTED_SHARED_PREF_NAME = "enc_shared_pref"

    val IGNORE_AUTH_URLS = listOf("/login", "/register")
}