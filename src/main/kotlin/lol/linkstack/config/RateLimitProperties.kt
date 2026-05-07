package lol.linkstack.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "linkstack.security.rate-limit")
data class RateLimitProperties(
    val loginMaxAttempts: Int = 5,
    val loginWindowSeconds: Long = 300
)
