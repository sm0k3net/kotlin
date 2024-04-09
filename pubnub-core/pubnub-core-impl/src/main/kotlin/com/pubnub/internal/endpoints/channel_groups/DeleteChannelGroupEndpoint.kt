package com.pubnub.internal.endpoints.channel_groups

import com.pubnub.api.PubNubError
import com.pubnub.api.PubNubException
import com.pubnub.api.enums.PNOperationType
import com.pubnub.api.models.consumer.channel_group.PNChannelGroupsDeleteGroupResult
import com.pubnub.api.retry.RetryableEndpointGroup
import com.pubnub.internal.EndpointCore
import com.pubnub.internal.PubNubCore
import retrofit2.Call
import retrofit2.Response

/**
 * @see [PubNubCore.deleteChannelGroup]
 */
class DeleteChannelGroupEndpoint internal constructor(
    pubnub: PubNubCore,
    override val channelGroup: String,
) : EndpointCore<Void, PNChannelGroupsDeleteGroupResult>(pubnub), DeleteChannelGroupInterface {
    override fun validateParams() {
        super.validateParams()
        if (channelGroup.isBlank()) {
            throw PubNubException(PubNubError.GROUP_MISSING)
        }
    }

    override fun getAffectedChannelGroups() = listOf(channelGroup)

    override fun doWork(queryParams: HashMap<String, String>): Call<Void> {
        return retrofitManager.channelGroupService
            .deleteChannelGroup(
                configuration.subscribeKey,
                channelGroup,
                queryParams,
            )
    }

    override fun createResponse(input: Response<Void>): PNChannelGroupsDeleteGroupResult = PNChannelGroupsDeleteGroupResult()

    override fun operationType() = PNOperationType.PNRemoveGroupOperation

    override fun getEndpointGroupName(): RetryableEndpointGroup = RetryableEndpointGroup.CHANNEL_GROUP
}
