package com.patagonia.app.data.util

import kotlinx.coroutines.delay

/**
 * Retry a suspending block with exponential backoff (D-17).
 *
 * @param times Maximum number of attempts
 * @param initialDelayMs Delay before the first retry in milliseconds
 * @param maxDelayMs Maximum delay cap in milliseconds
 * @param factor Multiplier applied after each retry
 * @param shouldRetry Predicate to decide if the exception is retryable
 * @param block The suspending operation to retry
 * @return The result from a successful execution
 * @throws Exception The last exception if all retries are exhausted
 */
suspend fun <T> retryWithBackoff(
    times: Int = 3,
    initialDelayMs: Long = 1_000L,
    maxDelayMs: Long = 30_000L,
    factor: Double = 2.0,
    shouldRetry: (Exception) -> Boolean = { true },
    block: suspend (attempt: Int) -> T
): T {
    require(times >= 1) { "Retry times must be at least 1" }
    require(initialDelayMs > 0) { "Initial delay must be positive" }
    require(factor >= 1.0) { "Factor must be >= 1.0" }

    var currentDelay = initialDelayMs
    var lastException: Exception? = null

    repeat(times) { attempt ->
        try {
            return block(attempt)
        } catch (e: Exception) {
            lastException = e
            if (!shouldRetry(e) || attempt == times - 1) {
                throw e
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
        }
    }

    // Should never reach here, but satisfy compiler
    throw lastException ?: IllegalStateException("Retry exhausted without exception")
}
