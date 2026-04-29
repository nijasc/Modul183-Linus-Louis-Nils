package lol.linkstack.dto

import jakarta.validation.constraints.Size

data class SignUpDto(
    @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters.") val username: String,
    @Size(min = 8, max = 256, message = "Password must be at least 8 characters long (256 max).") val password: String
)