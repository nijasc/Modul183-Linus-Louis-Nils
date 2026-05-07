package lol.linkstack.service

import lol.linkstack.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userJpa: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return userJpa.findByNameIgnoreCase(username)
            ?: throw UsernameNotFoundException("Could not find user with username $username")
    }
}
