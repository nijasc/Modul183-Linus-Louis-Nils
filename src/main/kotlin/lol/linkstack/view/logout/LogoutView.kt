package lol.linkstack.view.logout

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.VaadinServletRequest
import com.vaadin.flow.server.VaadinServletResponse
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct
import lol.linkstack.constants.CssProperty
import lol.linkstack.constants.CssToken
import lol.linkstack.constants.Routes
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler

@Route(value = "logout", autoLayout = false)
@AnonymousAllowed
class LogoutView : VerticalLayout() {

    @PostConstruct
    fun init() {
        setSizeFull()
        alignItems = FlexComponent.Alignment.CENTER
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        style.set(CssProperty.BACKGROUND, CssToken.GRADIENT_PRIMARY_SECONDARY)

        val card = VerticalLayout().apply {
            isPadding = true
            isSpacing = true
            alignItems = FlexComponent.Alignment.CENTER
            setWidth(CARD_WIDTH)
            style.set(CssProperty.BACKGROUND, CssToken.LUMO_BASE_COLOR)
            style.set(CssProperty.BORDER, "1px solid ${CssToken.LUMO_CONTRAST_10PCT}")
            style.set(CssProperty.BORDER_RADIUS, "12px")
            style.set(CssProperty.PADDING, "2rem")
            style.set(CssProperty.BOX_SHADOW, CssToken.SHADOW_CARD)
        }

        if (isAuthenticated()) {
            card.add(
                Icon(VaadinIcon.SIGN_OUT).apply {
                    style.set(CssProperty.FONT_SIZE, ICON_SIZE)
                    style.set(CssProperty.COLOR, CssToken.LUMO_PRIMARY_COLOR)
                },
                H2("Sign out"),
                Paragraph("You're about to sign out of your account.").apply {
                    style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
                    style.set(CssProperty.TEXT_ALIGN, "center")
                },
                buildActionRow()
            )
        } else {
            card.add(
                Icon(VaadinIcon.CHECK_CIRCLE).apply {
                    style.set(CssProperty.FONT_SIZE, ICON_SIZE)
                    style.set(CssProperty.COLOR, CssToken.LUMO_SUCCESS_COLOR)
                },
                H2("You are signed out"),
                Paragraph("Thanks for visiting Linkstack. See you next time!").apply {
                    style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
                    style.set(CssProperty.TEXT_ALIGN, "center")
                },
                Button("Back to Home", Icon(VaadinIcon.HOME)).apply {
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    addClickListener { ui.ifPresent { it.page.setLocation(Routes.HOME) } }
                }
            )
        }
        add(card)
    }

    private fun buildActionRow(): HorizontalLayout {
        val cancelButton = Button("Cancel").apply {
            addClickListener { ui.ifPresent { it.page.setLocation(Routes.HOME) } }
        }
        val confirmButton = Button("Sign out", Icon(VaadinIcon.SIGN_OUT)).apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR)
            addClickListener { performLogout() }
        }
        return HorizontalLayout(cancelButton, confirmButton).apply {
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            isSpacing = true
        }
    }

    private fun performLogout() {
        val request = VaadinServletRequest.getCurrent()?.httpServletRequest
        val response = VaadinServletResponse.getCurrent()?.httpServletResponse
        val auth = SecurityContextHolder.getContext().authentication
        if (request != null && response != null) {
            SecurityContextLogoutHandler().logout(request, response, auth)
        } else {
            SecurityContextHolder.clearContext()
        }
        ui.ifPresent { it.page.setLocation(LOGIN_AFTER_LOGOUT) }
    }

    private fun isAuthenticated(): Boolean {
        val auth = SecurityContextHolder.getContext().authentication ?: return false
        return auth.isAuthenticated && auth.name != ANONYMOUS
    }

    companion object {
        private const val CARD_WIDTH = "420px"
        private const val ICON_SIZE = "2.5rem"
        private const val ANONYMOUS = "anonymousUser"
        private const val LOGIN_AFTER_LOGOUT = "${Routes.LOGIN}?logout"
    }
}
