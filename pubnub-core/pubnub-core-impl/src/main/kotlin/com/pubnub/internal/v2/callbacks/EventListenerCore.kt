package com.pubnub.internal.v2.callbacks

import com.pubnub.api.BasePubNub
import com.pubnub.api.callbacks.Listener
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import com.pubnub.internal.models.consumer.pubsub.objects.PNObjectEventResult

interface EventListenerCore : Listener {
    /**
     * Receive messages at subscribed channels.
     *
     * @see [PubNub.subscribe]
     *
     * @param pubnub The client instance which has this listener attached.
     * @param event Wrapper around the actual message content.
     */
    fun message(
        pubnub: BasePubNub<*, *, *, *, *, *, *, *>,
        event: PNMessageResult,
    ) {}

    /**
     * Receive presence events for channels subscribed with presence enabled via
     * passing [com.pubnub.api.v2.subscriptions.SubscriptionOptions.receivePresenceEvents]
     * in [com.pubnub.api.v2.entities.BaseChannel.subscription].
     *
     * @param pubnub The client instance which has this listener attached.
     * @param event Wrapper around a presence event.
     */
    fun presence(
        pubnub: BasePubNub<*, *, *, *, *, *, *, *>,
        event: PNPresenceEventResult,
    ) {}

    /**
     * Receive signals at subscribed channels.
     *
     * @see [PubNub.signal]
     *
     * @param pubnub The client instance which has this listener attached.
     * @param event Wrapper around a signal event.
     */
    fun signal(
        pubnub: BasePubNub<*, *, *, *, *, *, *, *>,
        event: PNSignalResult,
    ) {}

    /**
     * Receive message actions for messages in subscribed channels.
     *
     * @param pubnub The client instance which has this listener attached.
     * @param event Wrapper around a message action event.
     */
    fun messageAction(
        pubnub: BasePubNub<*, *, *, *, *, *, *, *>,
        event: PNMessageActionResult,
    ) {}

    /**
     * Receive channel metadata and UUID metadata events in subscribed channels.
     *
     * @param pubnub The client instance which has this listener attached.
     * @param event Wrapper around the object event.
     */
    fun objects(
        pubnub: BasePubNub<*, *, *, *, *, *, *, *>,
        event: PNObjectEventResult,
    ) {}

    /**
     * Receive file events in subscribed channels.
     *
     * @param pubnub The client instance which has this listener attached.
     * @param event Wrapper around the file event.
     */
    fun file(
        pubnub: BasePubNub<*, *, *, *, *, *, *, *>,
        event: PNFileEventResult,
    ) {}
}
