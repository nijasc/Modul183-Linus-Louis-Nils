package lol.linkstack.view.page.component

import com.vaadin.flow.component.card.Card
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import lol.linkstack.constants.*
import lol.linkstack.dto.page.LinkDto

class PageLinksComponent(
    links: List<LinkDto>,
    defaultIconColor: String = PageDefaults.ICON_COLOR,
    textColor: String = PageDefaults.TEXT_COLOR
) : VerticalLayout() {
    init {
        isPadding = false
        defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
        style.set(CssProperty.COLOR, textColor)
        links.forEach { add(LinkCardComponent(it, defaultIconColor, textColor)) }
    }
}

class LinkCardComponent(
    link: LinkDto,
    defaultIconColor: String = PageDefaults.ICON_COLOR,
    textColor: String = PageDefaults.TEXT_COLOR
) : Card() {
    init {
        style.set(CssProperty.TRANSITION, "transform 0.2s, box-shadow 0.2s")
        style.set(CssProperty.CURSOR, "pointer")
        style.set(CssProperty.COLOR, textColor)
        style.set(CssProperty.MARGIN, "0.5rem")
        width = CARD_WIDTH

        element.addEventListener(CssEvent.MOUSE_ENTER) {
            style.set(CssProperty.TRANSFORM, "translateY(-2px)")
            style.set(CssProperty.BOX_SHADOW, CssToken.SHADOW_MEDIUM)
        }
        element.addEventListener(CssEvent.MOUSE_LEAVE) {
            style.set(CssProperty.TRANSFORM, "translateY(0)")
            style.set(CssProperty.BOX_SHADOW, "0 2px 6px rgba(0,0,0,0.1)")
        }

        add(
            HorizontalLayout(
                Icon(link.icon).apply {
                    style.set(CssProperty.COLOR, link.iconColor.ifBlank { defaultIconColor })
                    style.set(CssProperty.MARGIN_RIGHT, "0.5rem")
                },
                Anchor(link.href, link.name).apply {
                    element.setAttribute(CssAttribute.TARGET, "_blank")
                    element.setAttribute(CssAttribute.REL, CssToken.LINK_REL_NOOPENER)
                    style.set(CssProperty.TEXT_DECORATION, "none")
                    style.set(CssProperty.FONT_WEIGHT, "500")
                    style.set(CssProperty.COLOR, textColor)
                }
            ).apply {
                isPadding = false
                alignItems = FlexComponent.Alignment.CENTER
            }
        )
    }

    companion object {
        private const val CARD_WIDTH = "320px"
    }
}
