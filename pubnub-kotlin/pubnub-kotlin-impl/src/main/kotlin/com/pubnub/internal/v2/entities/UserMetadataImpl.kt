package com.pubnub.internal.v2.entities

import com.pubnub.api.v2.callbacks.EventListener
import com.pubnub.api.v2.entities.UserMetadata
import com.pubnub.api.v2.subscriptions.Subscription
import com.pubnub.internal.PubNubImpl
import com.pubnub.internal.v2.subscription.SubscriptionImpl

class UserMetadataImpl(pubnub: PubNubImpl, channelName: ChannelName) :
    BaseUserMetadataImpl<EventListener, Subscription>(
        pubnub.pubNubCore,
        channelName,
        { channels, channelGroups, options -> SubscriptionImpl(pubnub, channels, channelGroups, options) },
    ),
    UserMetadata
