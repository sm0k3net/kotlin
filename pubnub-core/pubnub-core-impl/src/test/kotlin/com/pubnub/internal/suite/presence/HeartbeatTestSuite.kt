package com.pubnub.internal.suite.presence

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.pubnub.api.enums.PNOperationType
import com.pubnub.internal.endpoints.presence.HeartbeatEndpoint
import org.junit.Assert.assertTrue

class HeartbeatTestSuite : com.pubnub.internal.suite.CoreEndpointTestSuite<HeartbeatEndpoint, Boolean>() {
    override fun pnOperation() = PNOperationType.PNHeartbeatOperation

    override fun requiredKeys() = com.pubnub.internal.suite.SUB + com.pubnub.internal.suite.AUTH

    override fun snippet(): HeartbeatEndpoint {
        return HeartbeatEndpoint(
            pubnub = pubnub,
            channels = listOf("ch1"),
        )
    }

    override fun verifyResultExpectations(result: Boolean) {
        assertTrue(result)
    }

    override fun successfulResponseBody() =
        """
        {  
            "status": 200,
            "message": "OK",  
            "service": "Presence"
        }
        """.trimIndent()

    override fun unsuccessfulResponseBodyList() = emptyList<String>()

    override fun mappingBuilder(): MappingBuilder =
        get(urlPathEqualTo("/v2/presence/sub-key/mySubscribeKey/channel/ch1/heartbeat"))
            .withQueryParam("heartbeat", equalTo(config.presenceTimeout.toString()))

    override fun affectedChannelsAndGroups() = listOf("ch1") to emptyList<String>()

    override fun voidResponse() = true
}
