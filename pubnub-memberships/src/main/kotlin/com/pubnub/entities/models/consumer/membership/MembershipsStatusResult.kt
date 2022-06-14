package com.pubnub.entities.models.consumer.membership

import com.pubnub.api.models.consumer.objects.member.PNMemberArrayResult
import com.pubnub.api.models.consumer.objects.membership.PNChannelMembershipArrayResult

data class MembershipsStatusResult(
    val status: Int
)

internal fun PNChannelMembershipArrayResult.toUserMembershipsResult(): MembershipsStatusResult {
    return MembershipsStatusResult(
        status = status
    )
}

internal fun PNMemberArrayResult.toSpaceMembershipResult(): MembershipsStatusResult {
    return MembershipsStatusResult(
        status = status
    )
}