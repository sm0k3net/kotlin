package com.pubnub.api.integration

import com.pubnub.test.CommonUtils
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

class SpecialCharsIntegrationTest : BaseIntegrationTest() {
    @Test
    @Ignore // TODO didn't work on master either
    fun testSpecialCharsPathAndUrl() {
        val expectedChannel = CommonUtils.randomChannel()
        val keyName = "special_char"
        val value = CommonUtils.getSpecialCharsMap().map { it.regular }.shuffled().joinToString("")

        server.publish(
            channel = expectedChannel,
            message = value,
            meta =
                mapOf(
                    keyName to value,
                ),
        ).apply {
//            TODO FIX queryParam += mapOf(
//                "za" to value,
//                "aa" to value,
//                "s" to value,
//                "Zz" to value,
//                "ZZZ" to value,
//                "123" to value
//            )
        }.sync()

//        }.async { result ->
//            assertFalse(status.error)
//            SignatureUtils.decomposeAndVerifySignature(server.configuration, status.clientRequest!!)
//            success.set(true)
//        }

        val messages =
            server.history(
                channel = expectedChannel,
                includeMeta = true,
            ).sync().messages

        assertEquals(1, messages.size)
        assertEquals(value, messages[0].meta!!.asJsonObject[keyName].asString)
        assertEquals(value, messages[0].entry.asString)
    }
}
