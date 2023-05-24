package com.pubnub.api.subscribe.eventengine.effect

import com.pubnub.api.PubNubException
import com.pubnub.api.endpoints.remoteaction.RemoteAction
import com.pubnub.api.eventengine.EventSink
import com.pubnub.api.eventengine.ManagedEffect
import com.pubnub.api.subscribe.eventengine.event.Event
import com.pubnub.api.subscribe.eventengine.event.SubscriptionCursor
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class HandshakeReconnectEffect(
    private val remoteAction: RemoteAction<SubscriptionCursor>,
    private val eventSink: EventSink,
    private val policy: RetryPolicy,
    private val executorService: ScheduledExecutorService,
    private val handshakeReconnectInvocation: SubscribeEffectInvocation.HandshakeReconnect,
) : ManagedEffect {

    @Transient
    private var scheduled: ScheduledFuture<*>? = null

    @Transient
    private var cancelled = false

    @Synchronized
    override fun runEffect() {
        if (cancelled) {
            return
        }

        val delay = policy.nextDelay(handshakeReconnectInvocation.attempts)
        if (delay == null) {
            eventSink.add(event = Event.HandshakeReconnectGiveUp(handshakeReconnectInvocation.reason ?: PubNubException("Unknown error")))
            return
        }

        scheduled = executorService.schedule({
            remoteAction.async { result, status ->
                if (status.error) {
                    eventSink.add(
                        Event.HandshakeReconnectFailure(
                            status.exception ?: PubNubException("Unknown error")
                        )
                    )
                } else {
                    eventSink.add(Event.HandshakeReconnectSuccess(result!!))
                }
            }
        }, delay.toMillis(), TimeUnit.MILLISECONDS)
    }

    @Synchronized
    override fun cancel() {
        if (cancelled) {
            return
        }
        cancelled = true
        remoteAction.silentCancel()
        scheduled?.cancel(true)
    }
}
