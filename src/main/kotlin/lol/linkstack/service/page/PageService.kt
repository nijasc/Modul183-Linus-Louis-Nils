package lol.linkstack.service.page

import com.vaadin.flow.router.NotFoundException
import lol.linkstack.dto.page.LinkDto
import lol.linkstack.dto.page.PageDto
import lol.linkstack.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class PageService(
    private val userJpa: UserRepository
) {
    fun pageExists(username: String): Boolean {
        if (!username.startsWith("@")) {
            return false
        }
        return userJpa.existsByNameIgnoreCase(username.substring(1))
    }

    fun getPage(username: String): PageDto {
        if (!pageExists(username)) throw NotFoundException()
        val user =
            userJpa.findByNameIgnoreCase(username.substring(1)) ?: throw IllegalStateException("User not defined!")

        return PageDto(
            links = user.page.links.map {
                LinkDto(
                    href = it.href,
                    name = it.name,
                    icon = it.icon
                )
            },
            views = user.page.views,
            owner = user.name
        )
    }
}