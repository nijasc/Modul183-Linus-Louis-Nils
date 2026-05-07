package lol.linkstack.security

import lol.linkstack.config.RateLimitProperties
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
class LoginRateLimiter(
    private val properties: RateLimitProperties
) {
    private val attemptsByKey = ConcurrentHashMap<String, AttemptWindow>()

    fun isBlocked(key: String): Boolean {
        val window = attemptsByKey[key] ?: return false
        if (window.isExpired()) {
            attemptsByKey.remove(key)
            return false
        }
        return window.count >= properties.loginMaxAttempts
    }

    fun recordFailure(key: String) {
        attemptsByKey.compute(key) { _, current ->
            if (current == null || current.isExpired()) {
                AttemptWindow(1, Instant.now().plusSeconds(properties.loginWindowSeconds))
            } else {
                current.copy(count = current.count + 1)
            }
        }
    }

    fun reset(key: String) {
        attemptsByKey.remove(key)
    }

    private data class AttemptWindow(val count: Int, val expiresAt: Instant) {
        fun isExpired(): Boolean = Instant.now().isAfter(expiresAt)
    }
}
