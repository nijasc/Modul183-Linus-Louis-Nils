package lol.linkstack.service.page

import lol.linkstack.TestSupport
import lol.linkstack.repository.CommentRepository
import lol.linkstack.repository.LikeRepository
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
class CommentServiceTest @Autowired constructor(
    private val commentService: CommentService,
    private val commentRepository: CommentRepository,
    private val likeRepository: LikeRepository,
    private val userRepository: UserRepository,
    private val pageRepository: PageRepository
) {

    @BeforeEach
    fun setUp() {
        likeRepository.deleteAll()
        commentRepository.deleteAll()
        userRepository.deleteAll()
        pageRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        TestSupport.clearAuth()
    }

    @Test
    fun `addComment trims content and persists it`() {
        val owner = TestSupport.createUser(userRepository, pageRepository, "owner")
        val visitor = TestSupport.createUser(userRepository, pageRepository, "visitor")
        TestSupport.authenticate(visitor)

        val saved = commentService.addComment(owner.name, "  Hello there  ")
        assertEquals("Hello there", saved.content)
        assertEquals(visitor.name, saved.authorName)
    }

    @Test
    fun `addComment rejects empty and overly long content`() {
        val owner = TestSupport.createUser(userRepository, pageRepository, "owner")
        val visitor = TestSupport.createUser(userRepository, pageRepository, "visitor")
        TestSupport.authenticate(visitor)

        assertThrows(IllegalArgumentException::class.java) {
            commentService.addComment(owner.name, "    ")
        }
        assertThrows(IllegalArgumentException::class.java) {
            commentService.addComment(owner.name, "a".repeat(501))
        }
    }

    @Test
    fun `addComment requires authentication`() {
        val owner = TestSupport.createUser(userRepository, pageRepository, "owner")
        TestSupport.clearAuth()
        assertThrows(AccessDeniedException::class.java) {
            commentService.addComment(owner.name, "hi")
        }
    }

    @Test
    fun `deleteComment allows author and page owner but rejects others`() {
        val owner = TestSupport.createUser(userRepository, pageRepository, "owner")
        val author = TestSupport.createUser(userRepository, pageRepository, "author")
        val stranger = TestSupport.createUser(userRepository, pageRepository, "stranger")

        TestSupport.authenticate(author)
        val comment = commentService.addComment(owner.name, "hi")

        TestSupport.authenticate(stranger)
        assertThrows(AccessDeniedException::class.java) {
            commentService.deleteComment(comment.id)
        }

        TestSupport.authenticate(owner)
        commentService.deleteComment(comment.id)
        assertFalse(commentRepository.existsById(comment.id))
    }

    @Test
    fun `updateComment only allowed by author`() {
        val owner = TestSupport.createUser(userRepository, pageRepository, "owner")
        val author = TestSupport.createUser(userRepository, pageRepository, "author")

        TestSupport.authenticate(author)
        val comment = commentService.addComment(owner.name, "v1")

        TestSupport.authenticate(owner)
        assertThrows(AccessDeniedException::class.java) {
            commentService.updateComment(comment.id, "v2")
        }

        TestSupport.authenticate(author)
        val updated = commentService.updateComment(comment.id, "v2")
        assertEquals("v2", updated.content)
    }

    @Test
    fun `toggleLike adds and removes a like`() {
        val owner = TestSupport.createUser(userRepository, pageRepository, "owner")
        val visitor = TestSupport.createUser(userRepository, pageRepository, "visitor")

        TestSupport.authenticate(visitor)
        val comment = commentService.addComment(owner.name, "hi")
        val likedFirst = commentService.toggleLike(comment.id)
        assertTrue(likedFirst)

        val likedAgain = commentService.toggleLike(comment.id)
        assertFalse(likedAgain)
    }

    @Test
    fun `getCommentsForPage returns newest first`() {
        val owner = TestSupport.createUser(userRepository, pageRepository, "owner")
        val visitor = TestSupport.createUser(userRepository, pageRepository, "visitor")

        TestSupport.authenticate(visitor)
        val first = commentService.addComment(owner.name, "first")
        Thread.sleep(10)
        val second = commentService.addComment(owner.name, "second")

        val list = commentService.getCommentsForPage(owner.name)
        assertEquals(2, list.size)
        assertEquals(second.id, list[0].id)
        assertEquals(first.id, list[1].id)
    }
}
