package lol.linkstack.service.user

import jakarta.persistence.EntityExistsException
import jakarta.validation.Valid
import lol.linkstack.constants.ValidationLimits
import lol.linkstack.dto.SignUpDto
import lol.linkstack.entity.page.PageEntity
import lol.linkstack.entity.user.UserEntity
import lol.linkstack.repository.PageRepository
import lol.linkstack.repository.UserRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
class UserService(
    private val jpa: UserRepository,
    private val pageJpa: PageRepository,
    private val pwEncoder: PasswordEncoder
) {

    @Transactional
    fun signUp(@Valid req: SignUpDto) {
        require(req.username.matches(Regex(ValidationLimits.USERNAME_PATTERN))) {
            "Username may only contain letters, digits and underscore"
        }
        if (jpa.existsByNameIgnoreCase(req.username)) {
            throw EntityExistsException("A user with the name ${req.username} already exists.")
        }
        val newPage = pageJpa.save(PageEntity())
        val newUser = jpa.save(
            UserEntity().apply {
                name = req.username
                passwordHash = pwEncoder.encode(req.password)
                    ?: throw IllegalStateException("Password encoding failed")
                page = newPage
            }
        )
        newPage.user = newUser
        pageJpa.save(newPage)
    }

    fun getProfile(): String {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw AccessDeniedException("Not authenticated")
        return auth.name
    }
}
