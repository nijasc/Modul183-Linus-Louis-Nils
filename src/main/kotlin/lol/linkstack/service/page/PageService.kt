package lol.linkstack.service.page

import com.vaadin.flow.router.NotFoundException
import jakarta.servlet.http.HttpServletRequest
import lol.linkstack.dto.page.LinkDto
import lol.linkstack.dto.page.PageDto
import lol.linkstack.entity.page.PageEntity
import lol.linkstack.entity.page.PageViewEntity
import lol.linkstack.entity.user.UserEntity
import lol.linkstack.repository.PageViewRepository
import lol.linkstack.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PageService(
    private val userJpa: UserRepository,
    private val pageViewJpa: PageViewRepository
) {
    private val log = LoggerFactory.getLogger(PageService::class.java)

    fun pageExists(username: String): Boolean {
        if (!username.startsWith(USERNAME_PREFIX)) return false
        return userJpa.existsByNameIgnoreCase(username.substring(1))
    }

    @Transactional
    fun getPageAndTrackView(username: String, request: HttpServletRequest): PageDto {
        val user = findUser(username)
        val page = user.page
        recordViewIfNew(page, request)
        return buildPageDto(user, page)
    }

    @Transactional(readOnly = true)
    fun getPage(username: String): PageDto {
        val user = findUser(username)
        return buildPageDto(user, user.page)
    }

    private fun recordViewIfNew(page: PageEntity, request: HttpServletRequest) {
        val pageId = page.id ?: return
        val ip = extractClientIp(request)
        if (pageViewJpa.existsByIpAddressAndPageId(ip, pageId)) return

        val view = PageViewEntity().apply {
            ipAddress = ip
            userAgent = request.getHeader(HEADER_USER_AGENT)?.take(USER_AGENT_MAX)
            referer = request.getHeader(HEADER_REFERER)?.take(REFERER_MAX)
            this.page = page
        }
        try {
            pageViewJpa.save(view)
        } catch (ex: Exception) {
            log.debug("Skipped duplicate page view for ip={} page={}", ip, pageId)
        }
    }

    private fun extractClientIp(request: HttpServletRequest): String {
        val forwarded = request.getHeader(HEADER_X_FORWARDED_FOR)
        return if (!forwarded.isNullOrBlank()) {
            forwarded.split(",").first().trim().take(IP_MAX)
        } else {
            request.remoteAddr.orEmpty().take(IP_MAX)
        }
    }

    private fun findUser(username: String): UserEntity {
        if (!pageExists(username)) throw NotFoundException()
        return userJpa.findByNameIgnoreCase(username.substring(1))
            ?: throw NotFoundException()
    }

    private fun buildPageDto(user: UserEntity, page: PageEntity): PageDto {
        val pageId = page.id ?: throw IllegalStateException("Page id missing")
        return PageDto(
            links = page.links.map { LinkDto(it.id, it.href, it.name, it.icon, it.iconColor) },
            views = pageViewJpa.countByPageId(pageId),
            owner = user.name,
            backgroundColor = page.backgroundColor,
            textColor = page.textColor,
            cardColor = page.cardColor,
            iconColor = page.iconColor,
            showComments = page.showComments
        )
    }

    companion object {
        private const val USERNAME_PREFIX = "@"
        private const val HEADER_X_FORWARDED_FOR = "X-Forwarded-For"
        private const val HEADER_USER_AGENT = "User-Agent"
        private const val HEADER_REFERER = "Referer"
        private const val USER_AGENT_MAX = 512
        private const val REFERER_MAX = 512
        private const val IP_MAX = 64
    }
}
