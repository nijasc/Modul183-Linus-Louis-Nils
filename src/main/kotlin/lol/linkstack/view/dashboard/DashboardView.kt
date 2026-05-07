package lol.linkstack.view.dashboard

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.card.Card
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.router.Route
import jakarta.annotation.PostConstruct
import jakarta.annotation.security.PermitAll
import lol.linkstack.constants.CssProperty
import lol.linkstack.constants.CssToken
import lol.linkstack.service.page.CommentService
import lol.linkstack.service.page.ManagePageService
import lol.linkstack.service.user.UserService
import lol.linkstack.view.component.SharePageDialog
import lol.linkstack.view.dashboard.component.CommentsManagementComponent
import lol.linkstack.view.dashboard.component.EditLinksComponent
import lol.linkstack.view.dashboard.component.StyleOptionsComponent

@Route(value = "dashboard")
@PermitAll
class DashboardView(
    private val userService: UserService,
    private val managePageService: ManagePageService,
    private val commentService: CommentService
) : VerticalLayout() {

    @PostConstruct
    fun init() {
        isPadding = false
        setWidthFull()

        val username = userService.getProfile()
        add(buildWelcomeCard(username))
        add(H3("Dashboard").apply { style.set(CssProperty.MARGIN, "0 1rem") })

        val pageDto = managePageService.getPage()

        val linksContent = EditLinksComponent(managePageService)
        val commentsContent = CommentsManagementComponent(
            commentService,
            textColor = pageDto.textColor,
            iconColor = pageDto.iconColor
        )
        val styleContent = StyleOptionsComponent(managePageService)

        val tabs = buildTabs(linksContent, commentsContent, styleContent)
        add(tabs, linksContent, commentsContent, styleContent)
    }

    private fun buildWelcomeCard(username: String): Card {
        val viewProfileButton = Button("View my profile", Icon(VaadinIcon.EXTERNAL_LINK)).apply {
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            addClickListener {
                UI.getCurrent().page.open("/$USERNAME_PREFIX$username", "_blank")
            }
        }
        val shareButton = Button("Share", Icon(VaadinIcon.SHARE)).apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            addClickListener { SharePageDialog(username).open() }
        }

        return Card().apply {
            width = "100%"
            style.set(CssProperty.PADDING, "1rem")
            style.set(CssProperty.MARGIN_BOTTOM, "1rem")
            add(
                HorizontalLayout(
                    HorizontalLayout(
                        Icon(VaadinIcon.USER_CARD).apply {
                            style.set(CssProperty.COLOR, CssToken.LUMO_PRIMARY_COLOR)
                        },
                        H2("Welcome back, $username!").apply { isMargin = false }
                    ).apply {
                        alignItems = FlexComponent.Alignment.CENTER
                        isSpacing = true
                    },
                    HorizontalLayout(viewProfileButton, shareButton).apply {
                        alignItems = FlexComponent.Alignment.CENTER
                        isSpacing = true
                    }
                ).apply {
                    setWidthFull()
                    alignItems = FlexComponent.Alignment.CENTER
                    justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
                    style.set(CssProperty.FLEX_WRAP, "wrap")
                }
            )
        }
    }

    private fun buildTabs(
        linksContent: Component,
        commentsContent: Component,
        styleContent: Component
    ): Tabs {
        val linksTab = Tab(VaadinIcon.LINK.create(), Span("Links"))
        val commentsTab = Tab(VaadinIcon.COMMENT_O.create(), Span("Comments"))
        val styleTab = Tab(VaadinIcon.PAINTBRUSH.create(), Span("Style"))

        linksContent.isVisible = true
        commentsContent.isVisible = false
        styleContent.isVisible = false

        return Tabs(linksTab, commentsTab, styleTab).apply {
            orientation = Tabs.Orientation.HORIZONTAL
            addSelectedChangeListener { event ->
                linksContent.isVisible = event.selectedTab == linksTab
                commentsContent.isVisible = event.selectedTab == commentsTab
                styleContent.isVisible = event.selectedTab == styleTab
            }
        }
    }

    companion object {
        private const val USERNAME_PREFIX = "@"
    }
}
