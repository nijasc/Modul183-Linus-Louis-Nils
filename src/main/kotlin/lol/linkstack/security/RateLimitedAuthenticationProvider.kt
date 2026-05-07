package lol.linkstack.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
class RateLimitedAuthenticationProvider(
    userDetailsService: UserDetailsService,
    passwordEncoder: PasswordEncoder,
    private val rateLimiter: LoginRateLimiter
) : AuthenticationProvider {

    private val delegate = DaoAuthenticationProvider(userDetailsService).also {
        it.setPasswordEncoder(passwordEncoder)
    }

    override fun authenticate(authentication: Authentication): Authentication {
        val key = rateLimitKey(authentication.name)
        if (rateLimiter.isBlocked(key)) {
            throw LockedException("Too many failed login attempts. Try again later.")
        }
        try {
            val result = delegate.authenticate(authentication)
            rateLimiter.reset(key)
            return result
        } catch (ex: BadCredentialsException) {
            rateLimiter.recordFailure(key)
            throw ex
        }
    }

    override fun supports(authentication: Class<*>): Boolean = delegate.supports(authentication)

    private fun rateLimitKey(username: String): String {
        val ip = currentRequest()?.let(::extractIp) ?: UNKNOWN
        return "$ip|${username.lowercase()}"
    }

    private fun currentRequest(): HttpServletRequest? {
        val attrs = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        return attrs?.request
    }

    private fun extractIp(request: HttpServletRequest): String {
        val forwarded = request.getHeader("X-Forwarded-For")
        return if (!forwarded.isNullOrBlank()) forwarded.split(",").first().trim()
        else request.remoteAddr ?: UNKNOWN
    }

    companion object {
        private const val UNKNOWN = "unknown"
    }
}
