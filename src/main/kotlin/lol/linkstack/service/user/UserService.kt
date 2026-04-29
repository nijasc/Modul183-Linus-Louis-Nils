package lol.linkstack.service.user

import jakarta.persistence.EntityExistsException
import jakarta.validation.Valid
import lol.linkstack.dto.SignUpDto
import lol.linkstack.entity.user.UserEntity
import lol.linkstack.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Validated
class UserService(private val jpa: UserRepository, private val pwEncoder: PasswordEncoder) {
    fun signUp(@Valid req: SignUpDto ) {
        if (jpa.existsByNameIgnoreCase(req.username)) {
            throw EntityExistsException("A user with the name ${req.username} already exists.")
        }

        val newUser = UserEntity().apply {
            name = req.username
            passwordHash = pwEncoder.encode(req.password)!!
        }
        jpa.save(newUser)
    }
}