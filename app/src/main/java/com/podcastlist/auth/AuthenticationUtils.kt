package com.podcastlist.auth

import android.util.Patterns
import java.util.regex.Pattern


const val INVALID_EMAIL_TEXT = "Please enter a valid email"
const val INVALID_PASSWORD_TEXT = "Invalid password, it should have at least 6 characters and include 1 digit, 1 lower case letter and 1 upper case letter."
const val INVALID_COMBINATION_TEXT = "Email and/or password is wrong"
const val RECOVERY_MAIL_TEXT = "Recovery email was sent"
const val PASSWORDS_NOT_MATCH_TEXT = "Passwords don't match"
const val SUCCESSFUL_LOGIN = "You logged in successfully!"
const val SUCCESSFUL_REGISTER = "Your account was registered successfully!"
const val SUCCESSFUL_EMAIL_CHANGE = "Email changed successfully!"
const val SUCCESSFUL_PASSWORD_CHANGE = "Password changed successfully!"
const val SUCCESSFUL_ACCOUNT_DELETE = "Account was deleted successfully!"

private const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$"

fun String.isEmailValid(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPasswordValid(): Boolean {
    return this.isNotBlank() &&
            Pattern.compile(PASS_PATTERN).matcher(this).matches()
}
