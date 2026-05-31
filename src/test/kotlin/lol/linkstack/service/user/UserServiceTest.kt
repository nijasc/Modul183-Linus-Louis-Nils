package lol.linkstack.service.user

import jakarta.persistence.EntityExistsException
import lol.linkstack.TestSupport
import lol.linkstack.dto.SignUpDto
import lol.linkstack.repository.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class UserServiceTest @Autowired constructor(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @AfterEach
    fun afterEach() {
        userRepository.deleteAll()
    }

    @Test
    fun `signUp creates user with bcrypt hashed password and a page`() {
        userService.signUp(SignUpDto("alice", "secretpw1"))

        val user = userRepository.findByNameIgnoreCase("alice")
        assertNotNull(user)
        assertNotNull(user!!.page.id)
        assertNotEquals("secretpw1", user.passwordHash)
        assertTrue(user.passwordHash.startsWith($$"$2"), "Password should be BCrypt encoded")
        assertTrue(passwordEncoder.matches("secretpw1", user.passwordHash))
    }

    @Test
    fun `signUp rejects duplicate username case-insensitively`() {
        userService.signUp(SignUpDto("Bob", "secretpw1"))
        assertThrows(EntityExistsException::class.java) {
            userService.signUp(SignUpDto("bob", "anotherpw1"))
        }
    }

    @Test
    fun `signUp rejects invalid username characters`() {
        assertThrows(jakarta.validation.ConstraintViolationException::class.java) {
            userService.signUp(SignUpDto("bad name!", "secretpw1"))
        }
    }

    @Test
    fun `signUp validates length constraints`() {
        assertThrows(jakarta.validation.ConstraintViolationException::class.java) {
            userService.signUp(SignUpDto("ab", "secretpw1"))
        }
        assertThrows(jakarta.validation.ConstraintViolationException::class.java) {
            userService.signUp(SignUpDto("validUser", "short"))
        }
    }

    @Test
    fun `signUp creates user with a linked page entity`() {
        userService.signUp(SignUpDto("charlie", "password123"))
        val user = userRepository.findByNameIgnoreCase("charlie")!!
        assertEquals(user.id, user.page.user?.id)
    }

    @Test
    fun `isUsernameAvailable returns true for free username`() {
        assertTrue(userService.isUsernameAvailable("freeUser"))
    }

    @Test
    fun `isUsernameAvailable returns false for taken username`() {
        userService.signUp(SignUpDto("takenUser", "password123"))
        assertFalse(userService.isUsernameAvailable("takenUser"))
    }

    @Test
    fun `isUsernameAvailable is case-insensitive`() {
        userService.signUp(SignUpDto("CaseName", "password123"))
        assertFalse(userService.isUsernameAvailable("casename"))
        assertFalse(userService.isUsernameAvailable("CASENAME"))
    }

    @Test
    fun `isUsernameAvailable returns false for invalid username`() {
        assertFalse(userService.isUsernameAvailable("bad name!"))
        assertFalse(userService.isUsernameAvailable("ab"))
    }

    @Test
    fun `changePassword updates password hash`() {
        userService.signUp(SignUpDto("diana", "oldPassword1"))
        val userBefore = userRepository.findByNameIgnoreCase("diana")!!
        val oldHash = userBefore.passwordHash

        TestSupport.authenticate(userBefore)
        userService.changePassword("oldPassword1", "newPassword2")

        val userAfter = userRepository.findByNameIgnoreCase("diana")!!
        assertNotEquals(oldHash, userAfter.passwordHash)
        assertTrue(passwordEncoder.matches("newPassword2", userAfter.passwordHash))
    }

    @Test
    fun `changePassword throws when old password is wrong`() {
        userService.signUp(SignUpDto("eve", "correctPw1"))
        val user = userRepository.findByNameIgnoreCase("eve")!!
        TestSupport.authenticate(user)

        assertThrows(org.springframework.security.access.AccessDeniedException::class.java) {
            userService.changePassword("wrongPw1", "newPassword2")
        }
    }

    @Test
    fun `deleteAccount removes user from database`() {
        userService.signUp(SignUpDto("frank", "password123"))
        val user = userRepository.findByNameIgnoreCase("frank")!!
        TestSupport.authenticate(user)

        userService.deleteAccount("password123")

        assertNull(userRepository.findByNameIgnoreCase("frank"))
    }

    @Test
    fun `deleteAccount throws when password is wrong`() {
        userService.signUp(SignUpDto("grace", "password123"))
        val user = userRepository.findByNameIgnoreCase("grace")!!
        TestSupport.authenticate(user)

        assertThrows(org.springframework.security.access.AccessDeniedException::class.java) {
            userService.deleteAccount("wrongPassword")
        }
    }
}