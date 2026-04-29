package lol.linkstack.entity.page

import com.vaadin.flow.component.icon.VaadinIcon
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import lol.linkstack.entity.BaseEntity

@Entity
class LinkEntity : BaseEntity() {
    var name: String = ""
    var href: String = ""
    @Enumerated(EnumType.STRING)
    var icon: VaadinIcon = VaadinIcon.EXTERNAL_LINK
}