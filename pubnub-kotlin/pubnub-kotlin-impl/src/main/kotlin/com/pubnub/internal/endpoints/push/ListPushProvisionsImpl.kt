package com.pubnub.internal.endpoints.push

import com.pubnub.api.endpoints.push.ListPushProvisions
import com.pubnub.api.models.consumer.push.PNPushListProvisionsResult
import com.pubnub.internal.EndpointImpl
import com.pubnub.internal.PubNubImpl

/**
 * @see [PubNubImpl.auditPushChannelProvisions]
 */
class ListPushProvisionsImpl internal constructor(listPushProvisions: ListPushProvisionsInterface) :
    ListPushProvisionsInterface by listPushProvisions,
    ListPushProvisions,
    EndpointImpl<PNPushListProvisionsResult>(listPushProvisions)
