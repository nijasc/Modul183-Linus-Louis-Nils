package lol.linkstack.service.page

import lol.linkstack.dto.page.LinkDto
import lol.linkstack.entity.page.LinkEntity
import lol.linkstack.entity.user.UserEntity
import lol.linkstack.repository.LinkRepository
import lol.linkstack.repository.PageRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ManagePageService(
    private val pageJpa: PageRepository,
    private val linkJpa: LinkRepository
) {
    fun getLinks(): List<LinkDto> {
        val page = getCurrentUser().page
        return page.links.map { LinkDto(it.id, it.href, it.name, it.icon) }
    }

    @Transactional
    fun addLink(link: LinkDto): LinkDto {
        val page = getCurrentUser().page
        val entity = LinkEntity().apply {
            name = link.name
            href = link.href
            icon = link.icon
            this.page = page
        }
        val saved = linkJpa.save(entity)
        page.links.add(saved)
        pageJpa.save(page)
        return LinkDto(saved.id, saved.href, saved.name, saved.icon)
    }

    @Transactional
    fun removeLink(linkId: UUID?) {
        if (linkId == null) return
        linkJpa.deleteById(linkId)
    }

    private fun getCurrentUser(): UserEntity {
        return SecurityContextHolder.getContext().authentication?.principal as? UserEntity
            ?: throw IllegalStateException("User not authenticated")
    }
}