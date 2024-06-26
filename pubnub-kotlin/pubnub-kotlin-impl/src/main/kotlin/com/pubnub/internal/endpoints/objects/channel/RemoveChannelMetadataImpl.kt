package com.pubnub.internal.endpoints.objects.channel

import com.pubnub.api.endpoints.remoteaction.ExtendedRemoteAction
import com.pubnub.api.endpoints.remoteaction.MappingRemoteAction
import com.pubnub.api.models.consumer.objects.PNRemoveMetadataResult
import com.pubnub.internal.DelegatingEndpoint

class RemoveChannelMetadataImpl internal constructor(removeChannelMetadata: RemoveChannelMetadataEndpoint) :
    DelegatingEndpoint<PNRemoveMetadataResult, com.pubnub.internal.models.consumer.objects.PNRemoveMetadataResult>(
        removeChannelMetadata,
    ),
    RemoveChannelMetadataInterface by removeChannelMetadata,
    com.pubnub.api.endpoints.objects.channel.RemoveChannelMetadata {
        override fun convertAction(
            remoteAction: ExtendedRemoteAction<com.pubnub.internal.models.consumer.objects.PNRemoveMetadataResult>,
        ): ExtendedRemoteAction<PNRemoveMetadataResult> {
            return MappingRemoteAction(remoteAction, PNRemoveMetadataResult::from)
        }
    }
