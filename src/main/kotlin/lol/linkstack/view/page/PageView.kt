package lol.linkstack.view.page

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.VaadinServletRequest
import com.vaadin.flow.server.auth.AnonymousAllowed
import lol.linkstack.constants.*
import lol.linkstack.dto.page.PageDto
import lol.linkstack.service.page.CommentService
import lol.linkstack.service.page.PageService
import lol.linkstack.view.component.PageCommentDrawer
import lol.linkstack.view.page.component.PageHeaderComponent
import lol.linkstack.view.page.component.PageLinksComponent

@Route("/", autoLayout = false)
@AnonymousAllowed
@CssImport("./styles/page-view.css")
class PageView(
    private val pageService: PageService,
    private val commentService: CommentService
) : VerticalLayout(), HasUrlParameter<String>, HasDynamicTitle {

    private var username = ""
    private var drawer: PageCommentDrawer? = null

    init {
        addClassName(CssClass.PAGE_ROOT)
        isPadding = false
        isSpacing = false
        justifyContentMode = FlexComponent.JustifyContentMode.START
        defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
    }

    override fun setParameter(before: BeforeEvent, value: String) {
        if (!pageService.pageExists(value)) {
            showNotFound(value)
            return
        }
        username = value
        val request = VaadinServletRequest.getCurrent()?.httpServletRequest
        val page = if (request != null) {
            pageService.getPageAndTrackView(value, request)
        } else {
            pageService.getPage(value)
        }
        renderPage(page)
    }

    private fun renderPage(page: PageDto) {
        removeAll()
        applyTheme(page)

        val content = VerticalLayout().apply {
            addClassName(CssClass.PAGE_CONTENT)
            isPadding = false
            isSpacing = false
            defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
            style.set(CssProperty.MAX_WIDTH, PAGE_MAX_WIDTH)
            style.set(CssProperty.WIDTH, "100%")
            style.set(CssProperty.MARGIN, "0 auto")
            style.set(CssProperty.PADDING, PAGE_PADDING)
        }
        content.add(PageHeaderComponent(page.owner, page.views, page.textColor))
        content.add(PageLinksComponent(page.links, page.iconColor, page.textColor))
        add(content)

        if (page.showComments) {
            renderCommentsDrawer(page)
        }
    }

    private fun applyTheme(page: PageDto) {
        style.set(CssProperty.BACKGROUND_COLOR, page.backgroundColor)
        style.set(CssProperty.COLOR, page.textColor)
        style.set(CssVar.BG_COLOR, page.backgroundColor)
        style.set(CssVar.TEXT_COLOR, page.textColor)
        style.set(CssVar.CARD_COLOR, page.cardColor)
        style.set(CssVar.ICON_COLOR, page.iconColor)
    }

    private fun renderCommentsDrawer(page: PageDto) {
        val handle = username.removePrefix(USERNAME_PREFIX)
        val newDrawer = PageCommentDrawer(
            commentService = commentService,
            pageOwnerUsername = handle,
            textColor = page.textColor,
            cardColor = page.cardColor,
            iconColor = page.iconColor,
            backgroundColor = page.backgroundColor
        )
        drawer = newDrawer
        add(newDrawer)

        val openCommentsButton = Button(Icon(VaadinIcon.COMMENT)).apply {
            addClassName(CssClass.COMMENTS_FAB)
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            element.setAttribute(CssAttribute.ARIA_LABEL, "Open comments")
            style.set(CssVar.CARD_COLOR, page.cardColor)
            style.set(CssVar.TEXT_COLOR, page.textColor)
            add(Span("Comments"))
            addClickListener { drawer?.open() }
        }
        add(openCommentsButton)
    }

    private fun showNotFound(username: String) {
        removeAll()
        style.set(CssProperty.BACKGROUND_COLOR, CssToken.LUMO_BASE_COLOR)
        style.set(CssProperty.COLOR, CssToken.LUMO_BODY_TEXT_COLOR)

        val card = Div().apply {
            style.set(CssProperty.PADDING, "2.5rem")
            style.set(CssProperty.BORDER_RADIUS, "16px")
            style.set(CssProperty.BACKGROUND, CssToken.LUMO_CONTRAST_5PCT)
            style.set(CssProperty.TEXT_ALIGN, "center")
            style.set(CssProperty.MAX_WIDTH, "420px")
            style.set(CssProperty.ANIMATION, CssToken.FADE_IN_ANIMATION_SHORT)
        }
        card.add(
            Icon(VaadinIcon.SEARCH).apply {
                style.set(CssProperty.FONT_SIZE, "2.5rem")
                style.set(CssProperty.COLOR, CssToken.LUMO_ERROR_COLOR)
            },
            H2("Page not found").apply { style.set(CssProperty.MARGIN, "0.75rem 0 0.25rem") },
            Span("No page exists for '$username'").apply {
                style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
            }
        )

        val wrapper = HorizontalLayout(card).apply {
            setSizeFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            alignItems = FlexComponent.Alignment.CENTER
        }
        style.set(CssProperty.MIN_HEIGHT, "100vh")
        add(wrapper)
    }

    override fun getPageTitle(): String {
        val handle = username.removePrefix(USERNAME_PREFIX)
        return if (handle.isBlank()) APP_NAME else "@$handle - $APP_NAME"
    }

    companion object {
        private const val APP_NAME = "Linkstack"
        private const val USERNAME_PREFIX = "@"
        private const val PAGE_MAX_WIDTH = "800px"
        private const val PAGE_PADDING = "3.5rem 1rem 4rem"
    }
}
