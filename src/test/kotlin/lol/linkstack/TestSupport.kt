package lol.linkstack

import lol.linkstack.entity.page.PageEntity
import lol.linkstack.entity.user.UserEntity
import lol.linkstack.repository.PageRepository
import lol.linkstack.repository.UserRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

object TestSupport {
    fun createUser(
        userRepository: UserRepository,
        pageRepository: PageRepository,
        username: String,
        password: String = "hashed-password"
    ): UserEntity {
        val page = pageRepository.save(PageEntity())
        val user = userRepository.save(
            UserEntity().apply {
                name = username
                passwordHash = password
                this.page = page
            }
        )
        page.user = user
        pageRepository.save(page)
        return user
    }

    fun authenticate(user: UserEntity) {
        val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities)
        SecurityContextHolder.getContext().authentication = auth
    }

    fun clearAuth() {
        SecurityContextHolder.clearContext()
    }
}
