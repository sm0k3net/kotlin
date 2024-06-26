package com.pubnub.internal.endpoints.channel_groups

import com.pubnub.api.endpoints.channel_groups.ListAllChannelGroup
import com.pubnub.api.models.consumer.channel_group.PNChannelGroupsListAllResult
import com.pubnub.internal.EndpointImpl
import com.pubnub.internal.PubNubImpl

/**
 * @see [PubNubImpl.listAllChannelGroups]
 */
class ListAllChannelGroupImpl internal constructor(listAllChannelGroup: ListAllChannelGroupInterface) :
    ListAllChannelGroupInterface by listAllChannelGroup,
    ListAllChannelGroup,
    EndpointImpl<PNChannelGroupsListAllResult>(listAllChannelGroup)
