package com.pubnub.internal.suite.history.counts

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.pubnub.api.enums.PNOperationType
import com.pubnub.api.models.consumer.history.PNMessageCountResult
import com.pubnub.internal.endpoints.MessageCountsEndpoint
import org.junit.Assert.assertEquals

class MessageCountsTestSuite : com.pubnub.internal.suite.CoreEndpointTestSuite<MessageCountsEndpoint, PNMessageCountResult>() {
    override fun pnOperation() = PNOperationType.PNMessageCountOperation

    override fun requiredKeys() = com.pubnub.internal.suite.SUB + com.pubnub.internal.suite.AUTH

    override fun snippet(): MessageCountsEndpoint =
        pubnub.messageCounts(
            channels = listOf("ch1"),
            channelsTimetoken = listOf(1588284000000),
        )

    override fun verifyResultExpectations(result: PNMessageCountResult) {
        assertEquals(1, result.channels.keys.size)
        assertEquals(5L, result.channels["ch1"])
    }

    override fun successfulResponseBody() =
        """
        {
         "status": 200,
         "error": false,
         "error_message": "",
         "channels": {
          "ch1": 5
         }
        }
        """.trimIndent()

    override fun unsuccessfulResponseBodyList() = emptyList<String>()

    override fun mappingBuilder(): MappingBuilder {
        return get(urlPathEqualTo("/v3/history/sub-key/mySubscribeKey/message-counts/ch1"))
            .withQueryParam("timetoken", equalTo("1588284000000"))
    }

    override fun affectedChannelsAndGroups() = listOf("ch1") to emptyList<String>()
}
