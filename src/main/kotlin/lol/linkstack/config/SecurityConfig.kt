package lol.linkstack.config

import com.vaadin.flow.spring.security.VaadinSecurityConfigurer
import lol.linkstack.repository.UserRepository
import lol.linkstack.service.UserDetailsServiceImpl
import lol.linkstack.view.login.LoginView
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig(private val userJpa: UserRepository) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .with(VaadinSecurityConfigurer.vaadin()) { vaadin ->
                vaadin.loginView(LoginView::class.java)
                vaadin.defaultSuccessUrl("/dashboard")
                vaadin.anyRequest {
                    it.permitAll()
                }
            }
            .logout { logout ->
                logout
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
            }
            .userDetailsService(UserDetailsServiceImpl(userJpa))

        return http.build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
