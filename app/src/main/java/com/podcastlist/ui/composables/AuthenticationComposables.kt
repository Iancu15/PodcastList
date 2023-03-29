package com.podcastlist.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.podcastlist.R

@Composable
fun EmailField(
    value: String,
    onEmailChange: (String) -> Unit
) {
    OutlinedTextField(
        singleLine = true,
        label = { Text(stringResource(R.string.email_label)) },
        onValueChange = { onEmailChange(it) },
        value = value,
        placeholder = { Text(stringResource(R.string.email_placeholder)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = stringResource(
            R.string.email_icon)
        ) },
        modifier = Modifier.fillMaxWidth(0.8f)
    )
}

@Composable
fun PasswordField(
    value: String,
    onPasswordChange: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val visualTransformation =
        if (isVisible) VisualTransformation.None else PasswordVisualTransformation()
    val iconId =
        if (isVisible) R.drawable.visibility_48px else R.drawable.visibility_off_48px

    OutlinedTextField(
        singleLine = true,
        label = { Text(stringResource(R.string.password_label)) },
        onValueChange = { onPasswordChange(it) },
        value = value,
        placeholder = { Text(stringResource(R.string.password_placeholder)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = stringResource(
            R.string.password_icon)
        ) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = stringResource(R.string.visibility_icon),
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        visualTransformation = visualTransformation,
        modifier = Modifier.fillMaxWidth(0.8f)
    )
}

@Composable
fun AuthButton(
    buttonText: String,
    authAction: () -> Unit
) {
    Button(
        onClick = {
            authAction()
        },
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(15))
            .height(40.dp)
    ) {
        Text(buttonText)
    }
}