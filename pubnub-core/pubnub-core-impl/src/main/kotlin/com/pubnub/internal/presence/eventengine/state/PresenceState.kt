package com.pubnub.internal.presence.eventengine.state

import com.pubnub.api.PubNubException
import com.pubnub.internal.eventengine.State
import com.pubnub.internal.eventengine.noTransition
import com.pubnub.internal.eventengine.transitionTo
import com.pubnub.internal.presence.eventengine.effect.PresenceEffectInvocation
import com.pubnub.internal.presence.eventengine.event.PresenceEvent

internal sealed class PresenceState : State<PresenceEffectInvocation, PresenceEvent, PresenceState> {
    object HeartbeatInactive : PresenceState() {
        override fun transition(event: PresenceEvent): Pair<PresenceState, Set<PresenceEffectInvocation>> {
            return when (event) {
                is PresenceEvent.Joined -> {
                    transitionTo(Heartbeating(event.channels, event.channelGroups))
                }

                else -> {
                    noTransition()
                }
            }
        }
    }

    class Heartbeating(
        channels: Set<String>,
        channelGroups: Set<String>,
    ) : PresenceState() {
        // toSet() is a must because we want to make sure that channels is immutable, and Handshaking constructor
        // doesn't prevent from providing "channels" that is mutable set.
        val channels = channels.toSet()
        val channelGroups = channelGroups.toSet()

        override fun onEntry(): Set<PresenceEffectInvocation> = setOf(PresenceEffectInvocation.Heartbeat(channels, channelGroups))

        override fun transition(event: PresenceEvent): Pair<PresenceState, Set<PresenceEffectInvocation>> {
            return when (event) {
                is PresenceEvent.LeftAll -> {
                    transitionTo(HeartbeatInactive, PresenceEffectInvocation.Leave(channels, channelGroups))
                }

                is PresenceEvent.Disconnect -> {
                    transitionTo(
                        HeartbeatStopped(channels, channelGroups),
                        PresenceEffectInvocation.Leave(channels, channelGroups),
                    )
                }

                is PresenceEvent.Joined -> {
                    transitionTo(Heartbeating(channels + event.channels, channelGroups + event.channelGroups))
                }

                is PresenceEvent.Left -> {
                    if ((channels - event.channels).isEmpty() && (channelGroups - event.channelGroups).isEmpty()) {
                        transitionTo(HeartbeatInactive, PresenceEffectInvocation.Leave(channels, channelGroups))
                    } else {
                        transitionTo(
                            Heartbeating(channels - event.channels, channelGroups - event.channelGroups),
                            PresenceEffectInvocation.Leave(event.channels, event.channelGroups),
                        )
                    }
                }

                is PresenceEvent.HeartbeatSuccess -> {
                    transitionTo(HeartbeatCooldown(channels, channelGroups))
                }

                is PresenceEvent.HeartbeatFailure -> {
                    transitionTo(HeartbeatFailed(channels, channelGroups, event.reason))
                }

                else -> {
                    noTransition()
                }
            }
        }
    }

    class HeartbeatStopped(
        channels: Set<String>,
        channelGroups: Set<String>,
    ) : PresenceState() {
        val channels = channels.toSet()
        val channelGroups = channelGroups.toSet()

        override fun transition(event: PresenceEvent): Pair<PresenceState, Set<PresenceEffectInvocation>> {
            return when (event) {
                is PresenceEvent.LeftAll -> {
                    transitionTo(HeartbeatInactive)
                }

                is PresenceEvent.Joined -> {
                    transitionTo(HeartbeatStopped(channels + event.channels, channelGroups + event.channelGroups))
                }

                is PresenceEvent.Left -> {
                    if ((channels - event.channels).isEmpty() && (channelGroups - event.channelGroups).isEmpty()) {
                        transitionTo(HeartbeatInactive)
                    } else {
                        transitionTo(HeartbeatStopped(channels - event.channels, channelGroups - event.channelGroups))
                    }
                }

                is PresenceEvent.Reconnect -> {
                    transitionTo(Heartbeating(channels, channelGroups))
                }

                else -> {
                    noTransition()
                }
            }
        }
    }

    class HeartbeatFailed(
        channels: Set<String>,
        channelGroups: Set<String>,
        val reason: PubNubException?,
    ) : PresenceState() {
        val channels = channels.toSet()
        val channelGroups = channelGroups.toSet()

        override fun transition(event: PresenceEvent): Pair<PresenceState, Set<PresenceEffectInvocation>> {
            return when (event) {
                is PresenceEvent.LeftAll -> {
                    transitionTo(HeartbeatInactive, PresenceEffectInvocation.Leave(channels, channelGroups))
                }

                is PresenceEvent.Joined -> {
                    transitionTo(Heartbeating(channels + event.channels, channelGroups + event.channelGroups))
                }

                is PresenceEvent.Left -> {
                    if ((channels - event.channels).isEmpty() && (channelGroups - event.channelGroups).isEmpty()) {
                        transitionTo(HeartbeatInactive, PresenceEffectInvocation.Leave(channels, channelGroups))
                    } else {
                        transitionTo(
                            Heartbeating(channels - event.channels, channelGroups - event.channelGroups),
                            PresenceEffectInvocation.Leave(event.channels, event.channelGroups),
                        )
                    }
                }

                is PresenceEvent.Reconnect -> {
                    transitionTo(Heartbeating(channels, channelGroups))
                }

                is PresenceEvent.Disconnect -> {
                    transitionTo(
                        HeartbeatStopped(channels, channelGroups),
                        PresenceEffectInvocation.Leave(channels, channelGroups),
                    )
                }

                else -> {
                    noTransition()
                }
            }
        }
    }

    class HeartbeatCooldown(
        channels: Set<String>,
        channelGroups: Set<String>,
    ) : PresenceState() {
        val channels = channels.toSet()
        val channelGroups = channelGroups.toSet()

        override fun onEntry(): Set<PresenceEffectInvocation> = setOf(PresenceEffectInvocation.Wait())

        override fun onExit(): Set<PresenceEffectInvocation> = setOf(PresenceEffectInvocation.CancelWait)

        override fun transition(event: PresenceEvent): Pair<PresenceState, Set<PresenceEffectInvocation>> {
            return when (event) {
                is PresenceEvent.Joined -> {
                    transitionTo(Heartbeating(channels + event.channels, channelGroups + event.channelGroups))
                }

                is PresenceEvent.Left -> {
                    if ((channels - event.channels).isEmpty() && (channelGroups - event.channelGroups).isEmpty()) {
                        transitionTo(HeartbeatInactive, PresenceEffectInvocation.Leave(channels, channelGroups))
                    } else {
                        transitionTo(
                            Heartbeating(channels - event.channels, channelGroups - event.channelGroups),
                            PresenceEffectInvocation.Leave(event.channels, event.channelGroups),
                        )
                    }
                }

                is PresenceEvent.TimesUp -> {
                    transitionTo(Heartbeating(channels, channelGroups))
                }

                is PresenceEvent.Disconnect -> {
                    transitionTo(
                        HeartbeatStopped(channels, channelGroups),
                        PresenceEffectInvocation.Leave(channels, channelGroups),
                    )
                }

                is PresenceEvent.LeftAll -> {
                    transitionTo(HeartbeatInactive, PresenceEffectInvocation.Leave(channels, channelGroups))
                }

                else -> {
                    noTransition()
                }
            }
        }
    }
}
