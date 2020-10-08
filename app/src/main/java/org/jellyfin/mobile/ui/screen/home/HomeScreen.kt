package org.jellyfin.mobile.ui.screen.home

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.font
import androidx.compose.ui.text.font.fontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jellyfin.mobile.R
import org.jellyfin.mobile.controller.LibraryController
import org.jellyfin.mobile.controller.ServerController
import org.jellyfin.mobile.ui.ScreenScaffold
import org.jellyfin.mobile.ui.screen.AbstractScreen

class HomeScreen(
    private val serverController: ServerController,
    private val libraryController: LibraryController,
) : AbstractScreen() {
    @Composable
    override fun Content() {
        val currentUser = serverController.userInfo ?: return
        val userDetailsState = remember { mutableStateOf(false) }
        val (userDetailsShown, showUserDetails) = userDetailsState
        ScreenScaffold(
            title = stringResource(R.string.app_name_short),
            titleFont = fontFamily(font(R.font.quicksand)),
            actions = {
                UserDetailsButton(
                    user = currentUser,
                    showUserDetails = showUserDetails,
                )
            },
        ) {
            Column {
                Text(
                    text = "Welcome, ${currentUser.name}",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h5,
                )
                UserViews(libraryController = libraryController)
            }
            if (userDetailsShown) {
                UserDetails(
                    serverController = serverController,
                    user = currentUser,
                    showUserDetails = showUserDetails,
                )
            }
        }
    }
}
