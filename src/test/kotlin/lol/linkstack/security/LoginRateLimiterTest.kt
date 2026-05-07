package lol.linkstack.security

import lol.linkstack.config.RateLimitProperties
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LoginRateLimiterTest {

    @Test
    fun `does not block within allowed attempts`() {
        val limiter = LoginRateLimiter(RateLimitProperties(loginMaxAttempts = 3, loginWindowSeconds = 60))
        repeat(2) { limiter.recordFailure("k") }
        assertFalse(limiter.isBlocked("k"))
    }

    @Test
    fun `blocks after reaching threshold`() {
        val limiter = LoginRateLimiter(RateLimitProperties(loginMaxAttempts = 3, loginWindowSeconds = 60))
        repeat(3) { limiter.recordFailure("k") }
        assertTrue(limiter.isBlocked("k"))
    }

    @Test
    fun `reset clears recorded failures`() {
        val limiter = LoginRateLimiter(RateLimitProperties(loginMaxAttempts = 2, loginWindowSeconds = 60))
        repeat(2) { limiter.recordFailure("k") }
        assertTrue(limiter.isBlocked("k"))
        limiter.reset("k")
        assertFalse(limiter.isBlocked("k"))
    }

    @Test
    fun `keys are isolated`() {
        val limiter = LoginRateLimiter(RateLimitProperties(loginMaxAttempts = 2, loginWindowSeconds = 60))
        repeat(2) { limiter.recordFailure("a") }
        assertTrue(limiter.isBlocked("a"))
        assertFalse(limiter.isBlocked("b"))
    }
}
