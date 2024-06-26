package com.pubnub.internal.endpoints.access

import com.pubnub.api.endpoints.HasOverridableConfig

interface GrantInterface : HasOverridableConfig {
    val read: Boolean
    val write: Boolean
    val manage: Boolean
    val delete: Boolean
    val get: Boolean
    val update: Boolean
    val join: Boolean
    val ttl: Int
    val authKeys: List<String>
    val channels: List<String>
    val channelGroups: List<String>
    val uuids: List<String>
}
