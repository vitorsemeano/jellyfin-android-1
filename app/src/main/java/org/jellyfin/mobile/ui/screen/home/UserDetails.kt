package org.jellyfin.mobile.ui.screen.home

import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import org.jellyfin.mobile.R
import org.jellyfin.mobile.controller.ServerController
import org.jellyfin.mobile.model.dto.UserInfo
import org.jellyfin.mobile.ui.ChipletButton
import org.jellyfin.mobile.ui.DefaultCornerRounding
import org.jellyfin.mobile.ui.utils.ApiUserImage
import org.jellyfin.mobile.utils.toast

@Composable
fun UserImage(modifier: Modifier = Modifier, user: UserInfo) {
    Surface(
        modifier = Modifier.size(56.dp).padding(8.dp).clip(CircleShape).then(modifier),
        color = MaterialTheme.colors.primaryVariant,
    ) {
        ApiUserImage(
            userInfo = user,
            modifier = Modifier.size(40.dp),
        )
    }
}

@Composable
fun UserDetailsButton(
    modifier: Modifier = Modifier,
    user: UserInfo,
    showUserDetails: (Boolean) -> Unit
) {
    UserImage(
        modifier = modifier.clickable(interactionState = remember { InteractionState() }) {
            showUserDetails(true)
        },
        user = user,
    )
}

@Composable
fun UserDetails(
    serverController: ServerController,
    user: UserInfo,
    showUserDetails: (Boolean) -> Unit
) {
    Popup(
        alignment = Alignment.TopCenter,
        isFocusable = true,
        onDismissRequest = {
            showUserDetails(false)
        },
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = DefaultCornerRounding,
        ) {
            Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserImage(user = user)
                    Text(
                        text = user.name,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    val context = ContextAmbient.current
                    ChipletButton(
                        text = stringResource(R.string.profile_button_text),
                        onClick = {
                            context.toast("Not implemented")
                        },
                    )
                    ChipletButton(
                        text = stringResource(R.string.logout_button_text),
                        onClick = serverController::tryLogout,
                    )
                }
            }
        }
    }
}
