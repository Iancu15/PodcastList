package com.podcastlist.auth

import android.util.Patterns
import java.util.regex.Pattern

const val INVALID_EMAIL_TEXT = "Please enter a valid email"
const val INVALID_PASSWORD_TEXT = "Invalid password, it should have at least six digits and include one digit, one lower case letter and one upper case letter."
const val INVALID_COMBINATION_TEXT = "Email or password is wrong"
const val RECOVERY_MAIL_TEXT = "Recovery email was sent"
const val PASSWORDS_NOT_MATCH_TEXT = "Passwords don't match"

private const val MIN_PASS_LENGTH = 6
private const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"

fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.isNotBlank() &&
            this.length >= MIN_PASS_LENGTH &&
            Pattern.compile(PASS_PATTERN).matcher(this).matches()
}
