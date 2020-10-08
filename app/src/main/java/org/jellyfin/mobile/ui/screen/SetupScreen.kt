package org.jellyfin.mobile.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.font
import androidx.compose.ui.text.font.fontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jellyfin.mobile.R
import org.jellyfin.mobile.controller.ServerController
import org.jellyfin.mobile.model.state.AuthState
import org.jellyfin.mobile.model.state.CheckUrlState
import org.jellyfin.mobile.ui.CenterRow
import org.jellyfin.mobile.ui.inject

class SetupScreen : AbstractScreen() {
    @Composable
    override fun Content() {
        val serverController: ServerController by inject()
        val serverSelectionState = mutableStateOf(false)
        val serverSelected by serverSelectionState
        Surface(color = MaterialTheme.colors.background) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            ) {
                LogoHeader()
                Crossfade(current = serverSelected) { serverSelected ->
                    if (!serverSelected) {
                        ServerSelection(
                            serverController = serverController,
                            serverSelectionState = serverSelectionState,
                        )
                    } else {
                        AuthInput(serverController = serverController)
                    }
                }
            }
        }
    }

    @Stable
    @Composable
    fun LogoHeader() {
        CenterRow {
            Image(
                asset = vectorResource(R.drawable.ic_launcher_foreground),
                modifier = Modifier.width(72.dp).height(72.dp).padding(top = 8.dp),
                contentScale = FixedScale(1.2f),
            )
            Text(
                text = stringResource(R.string.app_name_short),
                modifier = Modifier.padding(vertical = 56.dp).padding(start = 12.dp, end = 24.dp),
                fontFamily = fontFamily(font(R.font.quicksand)),
                maxLines = 1,
                style = MaterialTheme.typography.h3,
            )
        }
    }

    @Composable
    fun ServerSelection(
        serverController: ServerController,
        serverSelectionState: MutableState<Boolean>
    ) {
        val coroutineScope = rememberCoroutineScope()
        val hostnameInputState = remember { mutableStateOf("") }
        val checkUrlState = remember { mutableStateOf<CheckUrlState>(CheckUrlState.Unchecked) }
        var hostname by hostnameInputState
        var urlStateDelegate by checkUrlState
        val error = urlStateDelegate as? CheckUrlState.Error

        ServerSelectionStateless(
            text = hostname,
            errorText = error?.run { stringResource(message) },
            onTextChange = { value ->
                urlStateDelegate = CheckUrlState.Unchecked
                hostname = value
            },
            loading = urlStateDelegate is CheckUrlState.Pending,
            submit = {
                coroutineScope.launch {
                    checkUrlState.value = CheckUrlState.Pending
                    serverController.checkServerUrl(hostnameInputState.value).let { state ->
                        checkUrlState.value = state
                        if (state is CheckUrlState.Success) {
                            serverSelectionState.value = true
                        }
                    }
                }
            },
        )
    }

    @Stable
    @Composable
    @OptIn(ExperimentalAnimationApi::class)
    private fun ServerSelectionStateless(
        text: String,
        errorText: String?,
        onTextChange: (String) -> Unit,
        loading: Boolean,
        submit: () -> Unit
    ) {
        Column {
            Text(
                text = stringResource(R.string.connect_to_server_title),
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.h5,
            )
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                label = {
                    Text(text = stringResource(R.string.host_input_hint))
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go,
                onImeActionPerformed = { _, _ ->
                    submit()
                },
                activeColor = MaterialTheme.colors.secondary,
                isErrorValue = errorText != null,
            )
            AnimatedVisibility(visible = errorText != null) {
                Text(
                    text = errorText.orEmpty(),
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                )
            }
            CenterRow {
                Button(
                    onClick = submit,
                    modifier = Modifier.padding(4.dp),
                    enabled = !loading,
                    backgroundColor = MaterialTheme.colors.secondary,
                ) {
                    Text(text = stringResource(R.string.connect_button_text))
                }
            }
        }
    }

    @Composable
    fun AuthInput(
        serverController: ServerController
    ) {
        val coroutineScope = rememberCoroutineScope()
        val authState = remember { mutableStateOf(AuthState.UNSET) }
        val usernameInputState = remember { mutableStateOf("") }
        val passwordInputState = remember { mutableStateOf("") }
        var authStateDelegate by authState
        var username by usernameInputState
        var password by passwordInputState

        AuthInputStateless(
            username = username,
            password = password,
            onUsernameChange = { user ->
                authStateDelegate = AuthState.UNSET
                username = user
            },
            onPasswordChange = { pw ->
                authStateDelegate = AuthState.UNSET
                password = pw
            },
            loading = authStateDelegate == AuthState.PENDING,
            error = authStateDelegate == AuthState.FAILURE,
            submit = submit@{
                if (username.isEmpty() || password.isEmpty())
                    return@submit

                coroutineScope.launch {
                    authStateDelegate = AuthState.PENDING
                    val authSuccess = serverController.authenticate(username, password)
                    authStateDelegate = if (authSuccess) AuthState.SUCCESS else AuthState.FAILURE
                }
            },
        )
    }

    @Stable
    @Composable
    @OptIn(ExperimentalFocus::class, ExperimentalAnimationApi::class)
    fun AuthInputStateless(
        username: String,
        password: String,
        onUsernameChange: (String) -> Unit,
        onPasswordChange: (String) -> Unit,
        loading: Boolean,
        error: Boolean,
        submit: () -> Unit
    ) {
        Column {
            val passwordFocusRequester = remember { FocusRequester() }
            Text(
                text = stringResource(R.string.connect_to_server_title),
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.h5,
            )
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = {
                    Text(text = stringResource(R.string.username_input_hint))
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                onImeActionPerformed = { _, _ ->
                    passwordFocusRequester.requestFocus()
                },
                activeColor = MaterialTheme.colors.secondary,
            )
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = {
                    Text(text = stringResource(R.string.password_input_hint))
                },
                modifier = Modifier.focusRequester(passwordFocusRequester).fillMaxWidth().padding(bottom = 8.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onImeActionPerformed = { _, _ ->
                    submit()
                },
                activeColor = MaterialTheme.colors.secondary,
            )
            AnimatedVisibility(visible = error) {
                Text(
                    text = stringResource(R.string.error_text_wrong_login),
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                )
            }
            CenterRow {
                Button(
                    onClick = submit,
                    modifier = Modifier.padding(4.dp),
                    enabled = !loading && username.isNotEmpty() && password.isNotEmpty(),
                    backgroundColor = MaterialTheme.colors.secondary,
                ) {
                    Text(text = stringResource(R.string.login_button_text))
                }
            }
        }
    }
}
