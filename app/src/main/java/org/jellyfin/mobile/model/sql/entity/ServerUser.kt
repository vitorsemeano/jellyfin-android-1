package org.jellyfin.mobile.model.sql.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ServerUser(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = UserEntity.SERVER_ID,
        entityColumn = ServerEntity.SERVER_ID
    )
    val server: ServerEntity
)
