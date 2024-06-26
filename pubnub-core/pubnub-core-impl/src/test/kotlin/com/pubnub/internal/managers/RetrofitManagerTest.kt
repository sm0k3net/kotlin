package com.pubnub.internal.managers

import com.pubnub.api.legacy.BaseTest
import com.pubnub.internal.interceptor.SignatureInterceptor
import org.junit.Assert
import org.junit.Test

class RetrofitManagerTest : BaseTest() {
    @Test
    fun `retrofit manager created from another has shared OkHttpClients data`() {
        val retrofitManager = RetrofitManager(pubnub, pubnub.configuration)

        val clonedRetrofitManager = RetrofitManager(retrofitManager, pubnub.configuration)

        Assert.assertEquals(
            retrofitManager.subscriptionClientInstance!!.dispatcher,
            clonedRetrofitManager.subscriptionClientInstance!!.dispatcher,
        )
        Assert.assertEquals(
            retrofitManager.transactionClientInstance!!.dispatcher,
            clonedRetrofitManager.transactionClientInstance!!.dispatcher,
        )
        Assert.assertEquals(
            retrofitManager.noSignatureClientInstance!!.dispatcher,
            clonedRetrofitManager.noSignatureClientInstance!!.dispatcher,
        )

        Assert.assertEquals(
            retrofitManager.subscriptionClientInstance!!.connectionPool,
            clonedRetrofitManager.subscriptionClientInstance!!.connectionPool,
        )
        Assert.assertEquals(
            retrofitManager.transactionClientInstance!!.connectionPool,
            clonedRetrofitManager.transactionClientInstance!!.connectionPool,
        )
        Assert.assertEquals(
            retrofitManager.noSignatureClientInstance!!.connectionPool,
            clonedRetrofitManager.noSignatureClientInstance!!.connectionPool,
        )
    }

    @Test
    fun `retrofit manager created from another has separate SignatureInterceptors`() {
        val retrofitManager = RetrofitManager(pubnub, pubnub.configuration)
        val clonedRetrofitManager = RetrofitManager(retrofitManager, pubnub.configuration)

        Assert.assertNotEquals(
            retrofitManager.transactionClientInstance!!.interceptors.single { it is SignatureInterceptor },
            clonedRetrofitManager.subscriptionClientInstance!!.interceptors.single { it is SignatureInterceptor },
        )
    }
}
