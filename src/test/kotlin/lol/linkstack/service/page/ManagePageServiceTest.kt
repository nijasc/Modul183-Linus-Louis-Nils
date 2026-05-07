package lol.linkstack.service.page

import com.vaadin.flow.component.icon.VaadinIcon
import lol.linkstack.TestSupport
import lol.linkstack.dto.page.LinkDto
import lol.linkstack.repository.LinkRepository
import lol.linkstack.repository.PageRepository
import lol.linkstack.repository.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.AccessDeniedException
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class ManagePageServiceTest @Autowired constructor(
    private val managePageService: ManagePageService,
    private val userRepository: UserRepository,
    private val pageRepository: PageRepository,
    private val linkRepository: LinkRepository
) {

    @BeforeEach
    fun setUp() {
        linkRepository.deleteAll()
        userRepository.deleteAll()
        pageRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        TestSupport.clearAuth()
    }

    @Test
    fun `addLink stores link with sanitized values`() {
        val user = TestSupport.createUser(userRepository, pageRepository, "alice")
        TestSupport.authenticate(user)

        val saved = managePageService.addLink(
            LinkDto(
                href = "  https://example.com  ",
                name = "  Example  ",
                icon = VaadinIcon.LINK,
                iconColor = "#ff0000"
            )
        )
        assertEquals("Example", saved.name)
        assertEquals("https://example.com", saved.href)
        assertEquals("#ff0000", saved.iconColor)
    }

    @Test
    fun `addLink rejects non-http schemes`() {
        val user = TestSupport.createUser(userRepository, pageRepository, "alice")
        TestSupport.authenticate(user)

        assertThrows(IllegalArgumentException::class.java) {
            managePageService.addLink(LinkDto(href = "javascript:alert(1)", name = "x", icon = VaadinIcon.LINK))
        }
        assertThrows(IllegalArgumentException::class.java) {
            managePageService.addLink(LinkDto(href = "ftp://example.com", name = "x", icon = VaadinIcon.LINK))
        }
    }

    @Test
    fun `addLink rejects URL without host`() {
        val user = TestSupport.createUser(userRepository, pageRepository, "alice")
        TestSupport.authenticate(user)
        assertThrows(IllegalArgumentException::class.java) {
            managePageService.addLink(LinkDto(href = "https://", name = "x", icon = VaadinIcon.LINK))
        }
    }

    @Test
    fun `addLink rejects empty name`() {
        val user = TestSupport.createUser(userRepository, pageRepository, "alice")
        TestSupport.authenticate(user)
        assertThrows(IllegalArgumentException::class.java) {
            managePageService.addLink(LinkDto(href = "https://example.com", name = " ", icon = VaadinIcon.LINK))
        }
    }

    @Test
    fun `addLink rejects invalid color`() {
        val user = TestSupport.createUser(userRepository, pageRepository, "alice")
        TestSupport.authenticate(user)
        assertThrows(IllegalArgumentException::class.java) {
            managePageService.addLink(
                LinkDto(href = "https://example.com", name = "x", icon = VaadinIcon.LINK, iconColor = "red")
            )
        }
    }

    @Test
    fun `removeLink prevents deleting links of other users`() {
        val alice = TestSupport.createUser(userRepository, pageRepository, "alice")
        val bob = TestSupport.createUser(userRepository, pageRepository, "bob")
        TestSupport.authenticate(alice)
        val aliceLink = managePageService.addLink(
            LinkDto(href = "https://a.example", name = "A", icon = VaadinIcon.LINK)
        )
        TestSupport.authenticate(bob)
        assertThrows(AccessDeniedException::class.java) {
            managePageService.removeLink(aliceLink.id)
        }
        TestSupport.authenticate(alice)
        assertTrue(managePageService.getLinks().any { it.id == aliceLink.id })
    }

    @Test
    fun `removeLink deletes own link`() {
        val alice = TestSupport.createUser(userRepository, pageRepository, "alice")
        TestSupport.authenticate(alice)
        val link = managePageService.addLink(
            LinkDto(href = "https://a.example", name = "A", icon = VaadinIcon.LINK)
        )
        managePageService.removeLink(link.id)
        assertTrue(managePageService.getLinks().isEmpty())
    }

    @Test
    fun `updatePageStyle persists colors`() {
        val alice = TestSupport.createUser(userRepository, pageRepository, "alice")
        TestSupport.authenticate(alice)
        managePageService.updatePageStyle("#000000", "#ffffff", "#abcdef", "#123456", false)
        val page = managePageService.getPage()
        assertEquals("#000000", page.backgroundColor)
        assertEquals("#ffffff", page.textColor)
        assertEquals("#abcdef", page.cardColor)
        assertEquals("#123456", page.iconColor)
        assertFalse(page.showComments)
    }

    @Test
    fun `updatePageStyle rejects invalid colors`() {
        val alice = TestSupport.createUser(userRepository, pageRepository, "alice")
        TestSupport.authenticate(alice)
        assertThrows(IllegalArgumentException::class.java) {
            managePageService.updatePageStyle("not-a-color", "#000000", "#000000", "#000000", true)
        }
    }

    @Test
    fun `getPage requires authenticated user`() {
        TestSupport.clearAuth()
        assertThrows(AccessDeniedException::class.java) {
            managePageService.getPage()
        }
    }
}
