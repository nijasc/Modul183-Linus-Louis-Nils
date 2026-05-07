package lol.linkstack.service.page

import lol.linkstack.constants.ValidationLimits
import lol.linkstack.dto.page.CommentDto
import lol.linkstack.entity.page.CommentEntity
import lol.linkstack.entity.page.LikeEntity
import lol.linkstack.entity.user.UserEntity
import lol.linkstack.repository.CommentRepository
import lol.linkstack.repository.LikeRepository
import lol.linkstack.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CommentService(
    private val commentJpa: CommentRepository,
    private val likeJpa: LikeRepository,
    private val userJpa: UserRepository
) {
    private val log = LoggerFactory.getLogger(CommentService::class.java)

    @Transactional(readOnly = true)
    fun getCommentsForPage(pageOwnerUsername: String): List<CommentDto> {
        val owner = requireOwner(pageOwnerUsername)
        val pageId = owner.page.id ?: throw IllegalStateException("Page id missing")
        val currentUser = currentUserOrNull()
        return commentJpa.findByPageIdOrderByCreatedAtDesc(pageId)
            .map { buildCommentDto(it, currentUser) }
    }

    @Transactional
    fun addComment(pageOwnerUsername: String, content: String): CommentDto {
        val trimmed = sanitizeComment(content)
        val author = requireCurrentUser()
        val owner = requireOwner(pageOwnerUsername)

        val saved = commentJpa.save(
            CommentEntity().apply {
                this.content = trimmed
                this.page = owner.page
                this.author = author
            }
        )
        log.debug("User {} commented on {}", author.name, pageOwnerUsername)
        return buildCommentDto(saved, author)
    }

    @Transactional
    fun deleteComment(commentId: UUID) {
        val currentUser = requireCurrentUser()
        val comment = commentJpa.findById(commentId)
            .orElseThrow { IllegalArgumentException("Comment not found") }
        if (!canModify(comment, currentUser)) {
            throw AccessDeniedException("You don't have permission to delete this comment")
        }
        commentJpa.delete(comment)
    }

    @Transactional
    fun updateComment(commentId: UUID, newContent: String): CommentDto {
        val trimmed = sanitizeComment(newContent)
        val currentUser = requireCurrentUser()
        val comment = commentJpa.findById(commentId)
            .orElseThrow { IllegalArgumentException("Comment not found") }
        if (comment.author?.id != currentUser.id) {
            throw AccessDeniedException("You can only edit your own comments")
        }
        comment.content = trimmed
        return buildCommentDto(commentJpa.save(comment), currentUser)
    }

    @Transactional
    fun toggleLike(commentId: UUID): Boolean {
        val currentUser = requireCurrentUser()
        val comment = commentJpa.findById(commentId)
            .orElseThrow { IllegalArgumentException("Comment not found") }
        val userId = currentUser.id ?: throw IllegalStateException("User id missing")

        return if (likeJpa.existsByCommentIdAndUserId(commentId, userId)) {
            likeJpa.deleteByCommentIdAndUserId(commentId, userId)
            false
        } else {
            likeJpa.save(
                LikeEntity().apply {
                    this.comment = comment
                    this.user = currentUser
                }
            )
            true
        }
    }

    @Transactional(readOnly = true)
    fun getCommentsOnCurrentUserPage(): List<CommentDto> {
        val currentUser = requireCurrentUser()
        val pageId = currentUser.page.id ?: return emptyList()
        return commentJpa.findByPageIdOrderByCreatedAtDesc(pageId)
            .map { buildCommentDto(it, currentUser) }
    }

    private fun sanitizeComment(content: String): String {
        val trimmed = content.trim()
        require(trimmed.isNotEmpty()) { "Comment cannot be empty" }
        require(trimmed.length <= ValidationLimits.COMMENT_MAX) {
            "Comment too long (max ${ValidationLimits.COMMENT_MAX} characters)"
        }
        return trimmed
    }

    private fun canModify(comment: CommentEntity, currentUser: UserEntity): Boolean {
        val isAuthor = comment.author?.id == currentUser.id
        val isPageOwner = comment.page?.user?.id == currentUser.id
        return isAuthor || isPageOwner
    }

    private fun buildCommentDto(comment: CommentEntity, currentUser: UserEntity?): CommentDto {
        val commentId = comment.id ?: throw IllegalStateException("Comment id missing")
        val likes = likeJpa.countByCommentId(commentId)
        val hasLiked = currentUser?.id?.let { likeJpa.existsByCommentIdAndUserId(commentId, it) } ?: false
        val pageOwnerId = comment.page?.user?.id
        val canDelete = currentUser != null &&
                (comment.author?.id == currentUser.id || pageOwnerId == currentUser.id)

        return CommentDto(
            id = commentId,
            content = comment.content,
            authorName = comment.author?.name ?: UNKNOWN_USER,
            authorId = comment.author?.id,
            pageOwnerName = comment.page?.user?.name ?: UNKNOWN_USER,
            createdAt = comment.createdAt,
            isOwnComment = comment.author?.id == currentUser?.id,
            canDelete = canDelete,
            likes = likes,
            hasLiked = hasLiked
        )
    }

    private fun requireOwner(username: String): UserEntity {
        return userJpa.findByNameIgnoreCase(username)
            ?: throw IllegalArgumentException("Page owner not found")
    }

    private fun requireCurrentUser(): UserEntity {
        val username = SecurityContextHolder.getContext().authentication?.name
            ?: throw AccessDeniedException("Not authenticated")
        if (username == ANONYMOUS) throw AccessDeniedException("Not authenticated")
        return userJpa.findByNameIgnoreCase(username)
            ?: throw AccessDeniedException("Authenticated user not found")
    }

    private fun currentUserOrNull(): UserEntity? {
        val username = SecurityContextHolder.getContext().authentication?.name
        if (username.isNullOrBlank() || username == ANONYMOUS) return null
        return userJpa.findByNameIgnoreCase(username)
    }

    companion object {
        private const val UNKNOWN_USER = "Unknown"
        private const val ANONYMOUS = "anonymousUser"
    }
}
