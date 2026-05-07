package lol.linkstack.entity.page

import jakarta.persistence.*
import lol.linkstack.entity.BaseEntity
import lol.linkstack.entity.user.UserEntity

@Entity
@Table(name = "likes")
class LikeEntity : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    var comment: CommentEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserEntity? = null
}
