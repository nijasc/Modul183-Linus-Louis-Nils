package lol.linkstack.view.page.component

import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import lol.linkstack.constants.CssClass
import lol.linkstack.constants.CssProperty
import lol.linkstack.constants.CssToken
import lol.linkstack.constants.PageDefaults

class PageHeaderComponent(
    username: String,
    views: Long,
    textColor: String = PageDefaults.TEXT_COLOR
) : VerticalLayout() {
    init {
        isPadding = false
        defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
        style.set(CssProperty.ANIMATION, CssToken.FADE_IN_ANIMATION)
        style.set(CssProperty.COLOR, textColor)

        add(
            H2(username).apply {
                isMargin = false
                style.set(CssProperty.FONT_SIZE, USERNAME_FONT_SIZE)
                style.set(CssProperty.MARGIN_BOTTOM, "0.5rem")
                style.set(CssProperty.COLOR, textColor)
            },
            HorizontalLayout(
                Icon(VaadinIcon.EYE).apply {
                    style.set(CssProperty.MARGIN_RIGHT, "0.25rem")
                    style.set(CssProperty.COLOR, textColor)
                },
                Span("$views views").apply {
                    style.set(CssProperty.FONT_SIZE, VIEWS_FONT_SIZE)
                    style.set(CssProperty.COLOR, textColor)
                }
            ).apply {
                addClassName(CssClass.VIEWS_PILL)
                isPadding = false
                alignItems = FlexComponent.Alignment.CENTER
                style.set(CssProperty.BACKGROUND_COLOR, CssToken.LUMO_PRIMARY_COLOR_10PCT)
                style.set(CssProperty.PADDING, "0.5rem 1rem")
                style.set(CssProperty.BORDER_RADIUS, "20px")
            }
        )
    }

    companion object {
        private const val USERNAME_FONT_SIZE = "2.5rem"
        private const val VIEWS_FONT_SIZE = "1.1rem"
    }
}
