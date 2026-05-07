package lol.linkstack.dto

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import lol.linkstack.constants.ValidationLimits

data class SignUpDto(
    @field:Size(
        min = ValidationLimits.USERNAME_MIN,
        max = ValidationLimits.USERNAME_MAX,
        message = "Username must be between 3 and 16 characters."
    )
    @field:Pattern(
        regexp = ValidationLimits.USERNAME_PATTERN,
        message = "Username may only contain letters, digits and underscore."
    )
    val username: String,

    @field:Size(
        min = ValidationLimits.PASSWORD_MIN,
        max = ValidationLimits.PASSWORD_MAX,
        message = "Password must be at least 8 characters long (256 max)."
    )
    val password: String
)
