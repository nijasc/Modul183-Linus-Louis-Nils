package lol.linkstack.repository

import lol.linkstack.entity.page.CommentEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CommentRepository : JpaRepository<CommentEntity, UUID> {
    fun findByPageIdOrderByCreatedAtDesc(pageId: UUID): List<CommentEntity>
    fun findByAuthorId(authorId: UUID): List<CommentEntity>
}
