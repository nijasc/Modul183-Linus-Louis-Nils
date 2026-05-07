package lol.linkstack.entity.page

import com.vaadin.flow.component.icon.VaadinIcon
import jakarta.persistence.*
import lol.linkstack.entity.BaseEntity

@Entity
@Table(name = "links")
class LinkEntity : BaseEntity() {
    @Column(name = "name")
    var name: String = ""

    @Column(name = "href")
    var href: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "icon")
    var icon: VaadinIcon = VaadinIcon.EXTERNAL_LINK

    @Column(name = "icon_color_hex")
    var iconColor: String = "#197de1"

    @ManyToOne
    @JoinColumn(name = "page_id")
    var page: PageEntity? = null
}