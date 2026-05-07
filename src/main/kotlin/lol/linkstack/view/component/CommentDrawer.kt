package lol.linkstack.view.component

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import lol.linkstack.constants.*
import lol.linkstack.service.page.CommentService
import lol.linkstack.view.page.component.CommentSectionComponent

class PageCommentDrawer(
    commentService: CommentService,
    pageOwnerUsername: String,
    private val textColor: String,
    private val cardColor: String,
    private val iconColor: String,
    private val backgroundColor: String
) : Div() {

    private val backdrop = Div().apply {
        addClassName(CssClass.DRAWER_BACKDROP)
        element.addEventListener(CssEvent.CLICK) { close() }
    }

    private val titleCount = Span().apply { addClassName(CssClass.DRAWER_COUNT) }

    private val section = CommentSectionComponent(
        commentService = commentService,
        pageOwnerUsername = pageOwnerUsername,
        textColor = textColor,
        cardColor = cardColor,
        iconColor = iconColor,
        onCountChanged = { count -> updateCount(count) }
    )

    init {
        addClassName(CssClass.COMMENT_DRAWER)
        element.setAttribute(CssAttribute.DATA_OPEN, FALSE)

        style.set(CssVar.TEXT_COLOR, textColor)
        style.set(CssVar.CARD_COLOR, cardColor)
        style.set(CssVar.ICON_COLOR, iconColor)
        style.set(CssVar.BG_COLOR, backgroundColor)
        style.set(CssProperty.BACKGROUND_COLOR, cardColor)
        style.set(CssProperty.COLOR, textColor)

        backdrop.style.set(CssVar.TEXT_COLOR, textColor)

        val title = H3("Comments").apply {
            addClassName(CssClass.DRAWER_TITLE)
            element.removeAttribute(CssAttribute.STYLE)
            style.set(CssProperty.COLOR, textColor)
        }
        titleCount.style.set(CssProperty.COLOR, textColor)

        val titleWrap = HorizontalLayout(title, titleCount).apply {
            isPadding = false
            isSpacing = true
            alignItems = FlexComponent.Alignment.CENTER
        }

        val closeButton = Button(Icon(VaadinIcon.CLOSE_SMALL)).apply {
            addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON)
            element.setAttribute(CssAttribute.ARIA_LABEL, "Close comments")
            style.set(CssProperty.COLOR, textColor)
            addClickListener { close() }
        }

        val header = Div(titleWrap, closeButton).apply {
            addClassName(CssClass.DRAWER_HEADER)
        }
        val body = Div(section.listContainer).apply {
            addClassName(CssClass.DRAWER_BODY)
        }
        val footer = Div(section.composeContainer).apply {
            addClassName(CssClass.DRAWER_FOOTER)
        }

        add(header, body, footer)
    }

    override fun onAttach(attachEvent: AttachEvent) {
        super.onAttach(attachEvent)
        attachEvent.ui.element.appendChild(backdrop.element)
    }

    fun open() {
        section.refresh()
        backdrop.element.setAttribute(CssAttribute.DATA_OPEN, TRUE)
        element.setAttribute(CssAttribute.DATA_OPEN, TRUE)
    }

    fun close() {
        backdrop.element.setAttribute(CssAttribute.DATA_OPEN, FALSE)
        element.setAttribute(CssAttribute.DATA_OPEN, FALSE)
    }

    private fun updateCount(count: Int) {
        titleCount.text = if (count == 0) "" else count.toString()
        titleCount.element.style.set(CssProperty.DISPLAY, if (count == 0) "none" else "inline-flex")
    }

    companion object {
        private const val TRUE = "true"
        private const val FALSE = "false"
    }
}
