package com.pubnub.api.retry

open class RetryableTestBase {
    // this is used for test that execute retryPolicy and thus take more time to execute
    protected fun enableLongRunningRetryTests(): Boolean {
        return false
    }
}
