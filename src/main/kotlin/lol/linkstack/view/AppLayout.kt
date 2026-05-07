package lol.linkstack.view

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.Layout
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct
import lol.linkstack.constants.CssProperty
import lol.linkstack.constants.Routes
import org.springframework.security.core.context.SecurityContextHolder

@Layout
@AnonymousAllowed
class AppLayout : AppLayout() {

    @PostConstruct
    fun init() {
        initNavbar()
    }

    private fun initNavbar() {
        val homeLink = Anchor(Routes.HOME, "Home").apply {
            style.setPaddingLeft(SIDE_PADDING)
        }

        val authLayout = HorizontalLayout().apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.END
            alignItems = FlexComponent.Alignment.CENTER
            style.setPaddingRight(SIDE_PADDING)
        }

        if (isLoggedIn()) {
            authLayout.add(
                Anchor(Routes.DASHBOARD, "Dashboard").apply {
                    style.set(CssProperty.DISPLAY, "flex")
                    style.set(CssProperty.ALIGN_ITEMS, "center")
                    style.set(CssProperty.HEIGHT, "100%")
                },
                Button("Logout", Icon(VaadinIcon.SIGN_OUT)) {
                    UI.getCurrent().page.setLocation(Routes.LOGOUT)
                }
            )
        } else {
            authLayout.add(
                Anchor(Routes.LOGIN, "Login"),
                Anchor(Routes.SIGNUP, "Sign Up")
            )
        }

        addToNavbar(homeLink, authLayout)
    }

    private fun isLoggedIn(): Boolean {
        val auth = SecurityContextHolder.getContext().authentication ?: return false
        return auth.isAuthenticated && auth.name != ANONYMOUS
    }

    companion object {
        private const val SIDE_PADDING = "1rem"
        private const val ANONYMOUS = "anonymousUser"
    }
}
