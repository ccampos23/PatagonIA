package com.patagonia.app.data.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RetryUtilTest {

    @Test
    fun `succeeds on first attempt without delay`() = runTest {
        val result = retryWithBackoff(
            times = 3,
            initialDelayMs = 100L
        ) { attempt ->
            "success-$attempt"
        }
        assertEquals("success-0", result)
    }

    @Test
    fun `retries on failure and succeeds on second attempt`() = runTest {
        var callCount = 0
        val result = retryWithBackoff(
            times = 3,
            initialDelayMs = 10L
        ) { attempt ->
            callCount++
            if (attempt == 0) throw RuntimeException("transient error")
            "recovered"
        }
        assertEquals("recovered", result)
        assertEquals(2, callCount)
    }

    @Test
    fun `throws last exception when all retries exhausted`() = runTest {
        try {
            retryWithBackoff(
                times = 3,
                initialDelayMs = 10L
            ) { _: Int ->
                throw RuntimeException("permanent failure")
            }
            fail("Expected exception")
        } catch (e: RuntimeException) {
            assertEquals("permanent failure", e.message)
        }
    }

    @Test
    fun `respects shouldRetry predicate and stops on non-retryable`() = runTest {
        var callCount = 0
        try {
            retryWithBackoff(
                times = 5,
                initialDelayMs = 10L,
                shouldRetry = { e -> e.message != "fatal" }
            ) { _: Int ->
                callCount++
                throw RuntimeException("fatal")
            }
            fail("Expected exception")
        } catch (e: RuntimeException) {
            assertEquals("fatal", e.message)
            assertEquals(1, callCount) // Only called once, no retry
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `requires at least 1 retry attempt`() = runTest {
        retryWithBackoff(times = 0) { _: Int -> "nope" }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `requires positive initial delay`() = runTest {
        retryWithBackoff(initialDelayMs = 0L) { _: Int -> "nope" }
    }

    @Test
    fun `single attempt does not retry`() = runTest {
        var callCount = 0
        try {
            retryWithBackoff(
                times = 1,
                initialDelayMs = 10L
            ) { _: Int ->
                callCount++
                throw RuntimeException("fail")
            }
            fail("Expected exception")
        } catch (e: RuntimeException) {
            assertEquals(1, callCount)
        }
    }
}
