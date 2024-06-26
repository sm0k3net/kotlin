package com.pubnub.internal.models.consumer.access_manager.sum

import com.pubnub.api.PubNubError
import com.pubnub.api.PubNubException
import com.pubnub.internal.SpaceId
import com.pubnub.internal.models.consumer.access_manager.v3.ChannelGrant
import com.pubnub.internal.models.consumer.access_manager.v3.PNChannelPatternGrant
import com.pubnub.internal.models.consumer.access_manager.v3.PNChannelResourceGrant
import com.pubnub.internal.models.consumer.access_manager.v3.PNGrant
import com.pubnub.internal.models.consumer.access_manager.v3.PNSpacePatternPermissionsGrant
import com.pubnub.internal.models.consumer.access_manager.v3.PNSpacePermissionsGrant

interface SpacePermissions : PNGrant {
    companion object {
        fun id(
            spaceId: SpaceId,
            read: Boolean = false,
            write: Boolean = false,
            manage: Boolean = false,
            delete: Boolean = false,
            get: Boolean = false,
            join: Boolean = false,
            update: Boolean = false,
        ): SpacePermissions =
            PNSpacePermissionsGrant(
                id = spaceId.value,
                read = read,
                write = write,
                manage = manage,
                delete = delete,
                get = get,
                join = join,
                update = update,
            )

        fun pattern(
            pattern: String,
            read: Boolean = false,
            write: Boolean = false,
            manage: Boolean = false,
            delete: Boolean = false,
            get: Boolean = false,
            join: Boolean = false,
            update: Boolean = false,
        ): SpacePermissions =
            PNSpacePatternPermissionsGrant(
                id = pattern,
                read = read,
                write = write,
                manage = manage,
                delete = delete,
                get = get,
                join = join,
                update = update,
            )
    }
}

fun SpacePermissions.toChannelGrant(): ChannelGrant {
    return when (this) {
        is PNSpacePermissionsGrant -> PNChannelResourceGrant(spacePermissions = this)
        is PNSpacePatternPermissionsGrant -> PNChannelPatternGrant(spacePermissions = this)
        else -> throw PubNubException(pubnubError = PubNubError.INVALID_ARGUMENTS)
    }
}
