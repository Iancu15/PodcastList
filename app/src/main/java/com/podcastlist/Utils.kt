package com.podcastlist

import android.content.res.Resources

enum class Screen {
    HOME, SETTINGS, LOGIN, SPLASH, SIGNUP, EDIT_ACCOUNT
}

val screenToTitleDict = mapOf(
    Screen.HOME to "Home",
    Screen.SETTINGS to "Settings",
    Screen.LOGIN to "Login",
    Screen.SIGNUP to "Sign up",
    Screen.EDIT_ACCOUNT to "Edit account information"
)

val screenToPathDict = mapOf(
    Screen.HOME to "home",
    Screen.SETTINGS to "settings",
    Screen.LOGIN to "login",
    Screen.SIGNUP to "signup",
    Screen.EDIT_ACCOUNT to "edit_account"
)

fun getString(id: Int): String {
    return Resources.getSystem().getString(id)
}