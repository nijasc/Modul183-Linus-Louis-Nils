package lol.linkstack.config

import com.vaadin.flow.spring.security.VaadinSecurityConfigurer
import lol.linkstack.constants.Routes
import lol.linkstack.security.RateLimitedAuthenticationProvider
import lol.linkstack.view.login.LoginView
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@EnableWebSecurity
@Configuration
@EnableConfigurationProperties(RateLimitProperties::class)
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authProvider: RateLimitedAuthenticationProvider
    ): SecurityFilterChain {
        http
            .with(VaadinSecurityConfigurer.vaadin()) { vaadin ->
                vaadin.loginView(LoginView::class.java)
                vaadin.defaultSuccessUrl(Routes.DASHBOARD)
                vaadin.anyRequest { it.permitAll() }
            }
            .csrf { csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            }
            .headers { headers ->
                headers
                    .frameOptions { it.deny() }
                    .contentSecurityPolicy { csp ->
                        csp.policyDirectives(CSP_POLICY)
                    }
                    .httpStrictTransportSecurity { hsts ->
                        hsts.includeSubDomains(true).maxAgeInSeconds(HSTS_MAX_AGE)
                    }
            }
            .logout { logout ->
                logout
                    .logoutSuccessUrl("${Routes.LOGIN}?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies(JSESSIONID)
            }
            .authenticationProvider(authProvider)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    companion object {
        private const val CSP_POLICY =
            "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline'; img-src 'self' data:; " +
                    "font-src 'self' data:; connect-src 'self' ws: wss:; frame-ancestors 'none'"
        private const val HSTS_MAX_AGE = 31536000L
        private const val JSESSIONID = "JSESSIONID"
    }
}
