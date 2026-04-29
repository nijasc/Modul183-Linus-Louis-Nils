package lol.linkstack.dto.page

import com.vaadin.flow.component.icon.VaadinIcon
import java.util.*

data class LinkDto(
    val id: UUID? = null,
    val href: String,
    val name: String,
    val icon: VaadinIcon
)
