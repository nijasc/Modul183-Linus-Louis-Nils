package lol.linkstack.entity.page

import jakarta.persistence.*
import lol.linkstack.entity.BaseEntity
import lol.linkstack.entity.user.UserEntity

@Entity
class PageEntity : BaseEntity() {
    var views: Int = 0

    @OneToOne
    @JoinColumn(name = "user_id")
    var user: UserEntity? = null

    @OneToMany(cascade = [CascadeType.REMOVE], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "page_id")
    var links: MutableSet<LinkEntity> = mutableSetOf()
}
