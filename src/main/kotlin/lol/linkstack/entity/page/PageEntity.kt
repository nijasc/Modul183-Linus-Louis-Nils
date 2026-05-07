package lol.linkstack.entity.page

import jakarta.persistence.*
import lol.linkstack.constants.PageDefaults
import lol.linkstack.entity.BaseEntity
import lol.linkstack.entity.user.UserEntity

@Entity
class PageEntity : BaseEntity() {

    @OneToOne
    @JoinColumn(name = "user_id")
    var user: UserEntity? = null

    @OneToMany(mappedBy = "page", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var links: MutableSet<LinkEntity> = mutableSetOf()

    @OneToMany(mappedBy = "page", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var comments: MutableSet<CommentEntity> = mutableSetOf()

    @OneToMany(mappedBy = "page", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var pageViews: MutableSet<PageViewEntity> = mutableSetOf()

    var backgroundColor: String = PageDefaults.BACKGROUND_COLOR
    var textColor: String = PageDefaults.TEXT_COLOR
    var cardColor: String = PageDefaults.CARD_COLOR
    var iconColor: String = PageDefaults.ICON_COLOR
    var showComments: Boolean = PageDefaults.SHOW_COMMENTS
}
