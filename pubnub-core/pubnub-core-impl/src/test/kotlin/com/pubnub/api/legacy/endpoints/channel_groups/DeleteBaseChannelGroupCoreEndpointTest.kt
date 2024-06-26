package com.pubnub.api.legacy.endpoints.channel_groups

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.any
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.findAll
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.noContent
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.pubnub.api.PubNubError
import com.pubnub.api.PubNubException
import com.pubnub.api.legacy.BaseTest
import com.pubnub.test.CommonUtils.assertPnException
import com.pubnub.test.CommonUtils.failTest
import org.awaitility.Awaitility
import org.hamcrest.core.IsEqual
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class DeleteBaseChannelGroupCoreEndpointTest : BaseTest() {
    @Test
    fun testSyncSuccess() {
        stubFor(
            get(urlPathEqualTo("/v1/channel-registration/sub-key/mySubscribeKey/channel-group/groupA/remove"))
                .willReturn(
                    aResponse().withBody(
                        """{"status":200,"message":"OK","payload":{},"service":"ChannelGroups"}""",
                    ),
                ),
        )

        pubnub.deleteChannelGroup(
            channelGroup = "groupA",
        ).sync()
    }

    @Test
    fun testSyncEmptyGroup() {
        stubFor(any(anyUrl()).willReturn(aResponse()))

        try {
            pubnub.deleteChannelGroup(
                channelGroup = " ",
            ).sync()
            failTest()
        } catch (e: PubNubException) {
            assertPnException(PubNubError.GROUP_MISSING, e)
        }
    }

    @Test
    fun testIsAuthRequiredSuccessSync() {
        stubFor(
            get(urlPathEqualTo("/v1/channel-registration/sub-key/mySubscribeKey/channel-group/groupA/remove"))
                .willReturn(
                    aResponse().withBody(
                        """{"status": 200, "message": "OK", "payload": {}, "service": "ChannelGroups"}""",
                    ),
                ),
        )

        config.authKey = "myKey"

        pubnub.deleteChannelGroup(
            channelGroup = "groupA",
        ).sync()

        val requests =
            findAll(getRequestedFor(urlMatching("/.*")))
        assertEquals(1, requests.size)
        assertEquals("myKey", requests[0].queryParameter("auth").firstValue())
    }

    @Test
    fun testMalformedResponse() {
        stubFor(
            get(urlPathEqualTo("/v1/channel-registration/sub-key/mySubscribeKey/channel-group/groupA/remove"))
                .willReturn(noContent()),
        )

        pubnub.deleteChannelGroup(
            channelGroup = "groupA",
        ).sync()
    }

    @Test
    fun testOperationTypeSuccessAsync() {
        stubFor(
            get(urlPathEqualTo("/v1/channel-registration/sub-key/mySubscribeKey/channel-group/groupA/remove"))
                .willReturn(
                    aResponse().withBody(
                        """{"status": 200, "message": "OK", "payload": {}, "service": "ChannelGroups"}""",
                    ),
                ),
        )

        val atomic = AtomicInteger(0)

        pubnub.deleteChannelGroup(
            channelGroup = "groupA",
        ).async { result ->
            assertFalse(result.isFailure)
            atomic.incrementAndGet()
        }

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAtomic(atomic, IsEqual.equalTo(1))
    }
}
