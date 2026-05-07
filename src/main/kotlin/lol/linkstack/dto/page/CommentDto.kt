package lol.linkstack.dto.page

import java.time.Instant
import java.util.*

data class CommentDto(
    val id: UUID,
    val content: String,
    val authorName: String,
    val authorId: UUID?,
    val pageOwnerName: String,
    val createdAt: Instant,
    val isOwnComment: Boolean,
    val canDelete: Boolean = false,
    val likes: Int = 0,
    val hasLiked: Boolean = false
)
