package lol.linkstack.entity.page

import jakarta.persistence.*

import lol.linkstack.entity.BaseEntity
import lol.linkstack.entity.user.UserEntity

@Entity
@Table(name = "comments")
class CommentEntity : BaseEntity() {
    @Column(name = "content", columnDefinition = "TEXT")
    var content: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    var page: PageEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    var author: UserEntity? = null

    @OneToMany(mappedBy = "comment", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var likes: MutableSet<LikeEntity> = mutableSetOf()
}
