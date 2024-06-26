package com.pubnub.internal.subscribe.eventengine.effect

import com.pubnub.api.endpoints.remoteaction.RemoteAction
import com.pubnub.api.models.consumer.pubsub.PNEvent
import com.pubnub.internal.eventengine.Effect
import com.pubnub.internal.eventengine.EffectFactory
import com.pubnub.internal.eventengine.Sink
import com.pubnub.internal.presence.eventengine.data.PresenceData
import com.pubnub.internal.subscribe.eventengine.effect.effectprovider.HandshakeProvider
import com.pubnub.internal.subscribe.eventengine.effect.effectprovider.ReceiveMessagesProvider
import com.pubnub.internal.subscribe.eventengine.event.SubscribeEvent
import com.pubnub.internal.subscribe.eventengine.event.SubscriptionCursor

internal data class ReceiveMessagesResult(
    val messages: List<PNEvent>,
    val subscriptionCursor: SubscriptionCursor,
)

internal class SubscribeEffectFactory(
    private val handshakeProvider: HandshakeProvider,
    private val receiveMessagesProvider: ReceiveMessagesProvider,
    private val subscribeEventSink: Sink<SubscribeEvent>,
    private val messagesConsumer: MessagesConsumer,
    private val statusConsumer: StatusConsumer,
    private val presenceData: PresenceData,
    private val sendStateWithSubscribe: Boolean,
) : EffectFactory<SubscribeEffectInvocation> {
    override fun create(effectInvocation: SubscribeEffectInvocation): Effect? {
        return when (effectInvocation) {
            is SubscribeEffectInvocation.EmitMessages -> {
                EmitMessagesEffect(messagesConsumer, effectInvocation.messages)
            }

            is SubscribeEffectInvocation.EmitStatus -> {
                EmitStatusEffect(statusConsumer, effectInvocation.status)
            }

            is SubscribeEffectInvocation.Handshake -> {
                val handshakeRemoteAction =
                    handshakeProvider.getHandshakeRemoteAction(
                        effectInvocation.channels,
                        effectInvocation.channelGroups,
                        if (sendStateWithSubscribe) {
                            presenceData.channelStates.filter { it.key in effectInvocation.channels }
                        } else {
                            null
                        },
                    )
                HandshakeEffect(handshakeRemoteAction, subscribeEventSink)
            }

            is SubscribeEffectInvocation.ReceiveMessages -> {
                val receiveMessagesRemoteAction: RemoteAction<ReceiveMessagesResult> =
                    receiveMessagesProvider.getReceiveMessagesRemoteAction(
                        effectInvocation.channels,
                        effectInvocation.channelGroups,
                        effectInvocation.subscriptionCursor,
                    )
                ReceiveMessagesEffect(receiveMessagesRemoteAction, subscribeEventSink)
            }

            SubscribeEffectInvocation.CancelHandshake,
            SubscribeEffectInvocation.CancelReceiveMessages,
            -> null
        }
    }
}
