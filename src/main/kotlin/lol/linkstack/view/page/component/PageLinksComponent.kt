package lol.linkstack.view.page.component

import com.vaadin.flow.component.card.Card
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import lol.linkstack.dto.page.LinkDto

class PageLinksComponent(links: List<LinkDto>) : VerticalLayout() {
    init {
        isPadding = false
        defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
        links.forEach { add(LinkCardComponent(it)) }
    }
}

class LinkCardComponent(link: LinkDto) : Card() {
    init {
        add(
            HorizontalLayout(
                Icon(link.icon),
                Anchor(link.href, link.name).apply { element.setAttribute("target", "_blank") }
            ).apply {
                isPadding = false
                alignItems = FlexComponent.Alignment.CENTER
            }
        )
        width = "300px"
    }
}
