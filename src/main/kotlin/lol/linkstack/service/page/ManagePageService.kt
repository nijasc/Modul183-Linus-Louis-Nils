package lol.linkstack.service.page

import lol.linkstack.constants.UrlSchemes
import lol.linkstack.constants.ValidationLimits
import lol.linkstack.dto.page.LinkDto
import lol.linkstack.dto.page.PageDto
import lol.linkstack.entity.page.LinkEntity
import lol.linkstack.entity.page.PageEntity
import lol.linkstack.entity.user.UserEntity
import lol.linkstack.repository.LinkRepository
import lol.linkstack.repository.PageRepository
import lol.linkstack.repository.PageViewRepository
import lol.linkstack.repository.UserRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI
import java.net.URISyntaxException
import java.util.*

@Service
class ManagePageService(
    private val pageJpa: PageRepository,
    private val linkJpa: LinkRepository,
    private val pageViewJpa: PageViewRepository,
    private val userJpa: UserRepository
) {

    @Transactional(readOnly = true)
    fun getLinks(): List<LinkDto> {
        val pageId = currentPageId()
        return linkJpa.findByPageId(pageId).map { it.toDto() }
    }

    @Transactional
    fun addLink(link: LinkDto): LinkDto {
        validateLinkInput(link)
        val page = loadCurrentPage()
        val saved = linkJpa.save(
            LinkEntity().apply {
                name = link.name.trim()
                href = link.href.trim()
                icon = link.icon
                iconColor = link.iconColor
                this.page = page
            }
        )
        return saved.toDto()
    }

    @Transactional
    fun removeLink(linkId: UUID?) {
        if (linkId == null) return
        val page = loadCurrentPage()
        val link = linkJpa.findById(linkId).orElse(null) ?: return
        if (link.page?.id != page.id) {
            throw AccessDeniedException("You can only delete links on your own page")
        }
        linkJpa.delete(link)
    }

    @Transactional(readOnly = true)
    fun getPage(): PageDto {
        val user = loadCurrentUser()
        val page = user.page
        val pageId = page.id ?: throw IllegalStateException("Page id missing")
        return PageDto(
            links = linkJpa.findByPageId(pageId).map { it.toDto() },
            views = pageViewJpa.countByPageId(pageId),
            owner = user.name,
            backgroundColor = page.backgroundColor,
            textColor = page.textColor,
            cardColor = page.cardColor,
            iconColor = page.iconColor,
            showComments = page.showComments
        )
    }

    @Transactional
    fun updatePageStyle(
        backgroundColor: String,
        textColor: String,
        cardColor: String,
        iconColor: String,
        showComments: Boolean
    ) {
        validateColor(backgroundColor)
        validateColor(textColor)
        validateColor(cardColor)
        validateColor(iconColor)

        val page = loadCurrentPage()
        page.backgroundColor = backgroundColor
        page.textColor = textColor
        page.cardColor = cardColor
        page.iconColor = iconColor
        page.showComments = showComments
        pageJpa.save(page)
    }

    private fun validateLinkInput(link: LinkDto) {
        require(link.name.isNotBlank()) { "Link name must not be empty" }
        require(link.name.length <= ValidationLimits.LINK_NAME_MAX) {
            "Link name too long (max ${ValidationLimits.LINK_NAME_MAX})"
        }
        require(link.href.length <= ValidationLimits.LINK_HREF_MAX) {
            "Link URL too long (max ${ValidationLimits.LINK_HREF_MAX})"
        }
        validateUrl(link.href)
        validateColor(link.iconColor)
    }

    private fun validateUrl(href: String) {
        val uri = try {
            URI(href.trim())
        } catch (ex: URISyntaxException) {
            throw IllegalArgumentException("Invalid URL")
        }
        val scheme = uri.scheme?.lowercase()
        require(scheme != null && scheme in UrlSchemes.ALLOWED) {
            "URL must start with http:// or https://"
        }
        require(!uri.host.isNullOrBlank()) { "URL must have a host" }
    }

    private fun validateColor(value: String) {
        require(value.matches(Regex(ValidationLimits.HEX_COLOR_PATTERN))) {
            "Invalid color value: $value"
        }
    }

    private fun loadCurrentPage(): PageEntity {
        return loadCurrentUser().page
    }

    private fun loadCurrentUser(): UserEntity {
        val username = currentUsername()
        return userJpa.findByNameIgnoreCase(username)
            ?: throw IllegalStateException("Authenticated user not found in database")
    }

    private fun currentPageId(): UUID {
        return loadCurrentUser().page.id
            ?: throw IllegalStateException("Page id missing")
    }

    private fun currentUsername(): String {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw AccessDeniedException("Not authenticated")
        val principal = auth.principal
        require(principal is UserEntity) { "Unexpected principal type" }
        return principal.username
    }

    private fun LinkEntity.toDto(): LinkDto = LinkDto(id, href, name, icon, iconColor)
}
