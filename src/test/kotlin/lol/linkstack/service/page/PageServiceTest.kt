package lol.linkstack.service.page

import com.vaadin.flow.router.NotFoundException
import jakarta.servlet.http.HttpServletRequest
import lol.linkstack.TestSupport
import lol.linkstack.repository.PageRepository
import lol.linkstack.repository.PageViewRepository
import lol.linkstack.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class PageServiceTest @Autowired constructor(
    private val pageService: PageService,
    private val pageViewRepository: PageViewRepository,
    private val userRepository: UserRepository,
    private val pageRepository: PageRepository
) {

    @BeforeEach
    fun cleanUp() {
        pageViewRepository.deleteAll()
        userRepository.deleteAll()
        pageRepository.deleteAll()
    }

    @Test
    fun `pageExists returns true for existing user with at-prefix`() {
        TestSupport.createUser(userRepository, pageRepository, "dave")
        assertTrue(pageService.pageExists("@dave"))
        assertTrue(pageService.pageExists("@DAVE"))
    }

    @Test
    fun `pageExists returns false without at-prefix`() {
        TestSupport.createUser(userRepository, pageRepository, "dave")
        assertFalse(pageService.pageExists("dave"))
    }

    @Test
    fun `pageExists returns false for unknown users`() {
        assertFalse(pageService.pageExists("@ghost"))
    }

    @Test
    fun `getPageAndTrackView records exactly one view per IP`() {
        TestSupport.createUser(userRepository, pageRepository, "ellen")
        val first = mockRequest("203.0.113.5")
        val second = mockRequest("203.0.113.5")

        val initial = pageService.getPageAndTrackView("@ellen", first)
        val again = pageService.getPageAndTrackView("@ellen", second)

        assertEquals(1L, initial.views)
        assertEquals(1L, again.views)
    }

    @Test
    fun `getPageAndTrackView counts distinct IPs separately`() {
        TestSupport.createUser(userRepository, pageRepository, "ellen")
        pageService.getPageAndTrackView("@ellen", mockRequest("10.0.0.1"))
        pageService.getPageAndTrackView("@ellen", mockRequest("10.0.0.2"))
        val result = pageService.getPageAndTrackView("@ellen", mockRequest("10.0.0.3"))
        assertEquals(3L, result.views)
    }

    @Test
    fun `getPageAndTrackView prefers X-Forwarded-For header for IP`() {
        TestSupport.createUser(userRepository, pageRepository, "ellen")
        val request = mock<HttpServletRequest>()
        whenever(request.getHeader("X-Forwarded-For")).thenReturn("198.51.100.7, 10.0.0.1")
        whenever(request.remoteAddr).thenReturn("10.0.0.1")
        whenever(request.getHeader("User-Agent")).thenReturn("UA")
        whenever(request.getHeader("Referer")).thenReturn(null)

        pageService.getPageAndTrackView("@ellen", request)

        val request2 = mock<HttpServletRequest>()
        whenever(request2.getHeader("X-Forwarded-For")).thenReturn("198.51.100.7")
        whenever(request2.remoteAddr).thenReturn("10.0.0.99")
        whenever(request2.getHeader("User-Agent")).thenReturn("UA")
        whenever(request2.getHeader("Referer")).thenReturn(null)
        val result = pageService.getPageAndTrackView("@ellen", request2)

        assertEquals(1L, result.views)
    }

    @Test
    fun `getPageAndTrackView returns links and page metadata`() {
        TestSupport.createUser(userRepository, pageRepository, "frank")
        val dto = pageService.getPageAndTrackView("@frank", mockRequest("1.1.1.1"))
        assertEquals("frank", dto.owner)
        assertTrue(dto.links.isEmpty())
    }

    @Test
    fun `getPageAndTrackView throws NotFound for missing user`() {
        assertThrows(NotFoundException::class.java) {
            pageService.getPageAndTrackView("@ghost", mockRequest("1.1.1.1"))
        }
    }

    private fun mockRequest(ip: String): HttpServletRequest {
        val request = mock<HttpServletRequest>()
        whenever(request.getHeader("X-Forwarded-For")).thenReturn(null)
        whenever(request.remoteAddr).thenReturn(ip)
        whenever(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0")
        whenever(request.getHeader("Referer")).thenReturn(null)
        return request
    }
}
