package lol.linkstack.entity.page

import jakarta.persistence.*
import lol.linkstack.entity.BaseEntity

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["ip_address", "page_id"])], name = "page_views")
class PageViewEntity : BaseEntity() {

    @Column(name = "ip_address", nullable = false, length = 64)
    var ipAddress: String = ""

    @Column(name = "user_agent", length = 512)
    var userAgent: String? = null

    @Column(name = "referer", length = 512)
    var referer: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    var page: PageEntity? = null
}
