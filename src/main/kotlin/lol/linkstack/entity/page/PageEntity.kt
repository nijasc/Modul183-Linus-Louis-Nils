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

    @OneToMany(mappedBy = "page", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var links: MutableSet<LinkEntity> = mutableSetOf()
}
