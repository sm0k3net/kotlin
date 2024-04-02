package com.pubnub.internal.endpoints.channel_groups

import com.pubnub.api.endpoints.remoteaction.ExtendedRemoteAction
import com.pubnub.api.models.consumer.channel_group.PNChannelGroupsAllChannelsResult

interface AllChannelsChannelGroupInterface : ExtendedRemoteAction<PNChannelGroupsAllChannelsResult> {
    val channelGroup: String
}