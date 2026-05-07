package lol.linkstack.repository

import lol.linkstack.entity.page.LikeEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LikeRepository : JpaRepository<LikeEntity, UUID> {
    fun existsByCommentIdAndUserId(commentId: UUID, userId: UUID): Boolean
    fun countByCommentId(commentId: UUID): Int
    fun deleteByCommentIdAndUserId(commentId: UUID, userId: UUID)
}
