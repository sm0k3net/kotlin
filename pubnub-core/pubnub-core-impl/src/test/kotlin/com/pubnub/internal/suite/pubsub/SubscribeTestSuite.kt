package com.pubnub.internal.suite.pubsub

import com.github.tomakehurst.wiremock.client.WireMock.absent
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.pubnub.api.enums.PNOperationType
import com.pubnub.internal.endpoints.pubsub.SubscribeEndpoint
import com.pubnub.internal.models.server.SubscribeEnvelope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class SubscribeTestSuite : com.pubnub.internal.suite.CoreEndpointTestSuite<SubscribeEndpoint, SubscribeEnvelope>() {
    override fun pnOperation() = PNOperationType.PNSubscribeOperation

    override fun requiredKeys() = com.pubnub.internal.suite.SUB + com.pubnub.internal.suite.AUTH

    override fun snippet(): SubscribeEndpoint {
        return SubscribeEndpoint(pubnub).apply {
            channels = listOf("ch1")
        }
    }

    override fun verifyResultExpectations(result: SubscribeEnvelope) {
        assertEquals(100, result.metadata.timetoken)
        assertEquals("1", result.metadata.region)
        assertTrue(result.messages.isEmpty())
    }

    override fun successfulResponseBody() =
        """
        {
          "t": {
            "t": "100",
            "r": 1
          },
          "m": []
        }
        """.trimIndent()

    override fun unsuccessfulResponseBodyList() = emptyList<String>()

    override fun mappingBuilder() =
        get(urlPathEqualTo("/v2/subscribe/mySubscribeKey/ch1/0"))
            .withQueryParam("tt", absent())
            .withQueryParam("tr", absent())
            .withQueryParam("filter-expr", absent())
            .withQueryParam("state", absent())
            .withQueryParam("channel-group", absent())
            .withQueryParam("heartbeat", equalTo("300"))

    override fun affectedChannelsAndGroups() = listOf("ch1") to emptyList<String>()
}
