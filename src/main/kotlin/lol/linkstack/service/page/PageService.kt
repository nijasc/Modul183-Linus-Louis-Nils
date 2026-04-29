package lol.linkstack.service.page

import com.vaadin.flow.router.NotFoundException
import lol.linkstack.dto.page.LinkDto
import lol.linkstack.dto.page.PageDto
import lol.linkstack.entity.user.UserEntity
import lol.linkstack.repository.PageRepository
import lol.linkstack.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PageService(
    private val userJpa: UserRepository,
    private val pageJpa: PageRepository
) {
    fun pageExists(username: String): Boolean {
        if (!username.startsWith("@")) return false
        return userJpa.existsByNameIgnoreCase(username.substring(1))
    }

    @Transactional
    fun getPageAndIncrementViews(username: String): PageDto {
        val user = findUser(username)
        val page = user.page
        page.views++
        pageJpa.save(page)
        return buildPageDto(user, page)
    }

    private fun findUser(username: String): UserEntity {
        if (!pageExists(username)) throw NotFoundException()
        return userJpa.findByNameIgnoreCase(username.substring(1))
            ?: throw IllegalStateException("User not defined!")
    }

    private fun buildPageDto(user: UserEntity, page: lol.linkstack.entity.page.PageEntity): PageDto {
        return PageDto(
            links = page.links.map { LinkDto(it.id, it.href, it.name, it.icon) },
            views = page.views,
            owner = user.name
        )
    }
}