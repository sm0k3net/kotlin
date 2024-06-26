package com.pubnub.internal.endpoints.access

import com.pubnub.api.endpoints.remoteaction.ExtendedRemoteAction
import com.pubnub.api.endpoints.remoteaction.MappingRemoteAction
import com.pubnub.api.models.consumer.access_manager.PNAccessManagerGrantResult
import com.pubnub.internal.DelegatingEndpoint
import com.pubnub.internal.PubNubImpl

/**
 * @see [PubNubImpl.grant]
 */
class GrantImpl internal constructor(grant: GrantEndpoint) :
    DelegatingEndpoint<PNAccessManagerGrantResult, com.pubnub.internal.models.consumer.access_manager.PNAccessManagerGrantResult>(
        grant,
    ),
    GrantInterface by grant,
    com.pubnub.api.endpoints.access.Grant {
        override fun convertAction(
            remoteAction: ExtendedRemoteAction<com.pubnub.internal.models.consumer.access_manager.PNAccessManagerGrantResult>,
        ): ExtendedRemoteAction<PNAccessManagerGrantResult> {
            return MappingRemoteAction(remoteAction, PNAccessManagerGrantResult.Companion::from)
        }
    }
