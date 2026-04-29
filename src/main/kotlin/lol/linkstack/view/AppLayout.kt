package lol.linkstack.view

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.Layout
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct

@Layout
@AnonymousAllowed
class AppLayout : AppLayout() {
    @PostConstruct
    fun init() {
        initNavbar()
        initDrawer()
    }

    private fun initNavbar() {
        val homeLink = Anchor("/", "Home")
        val authLayout = HorizontalLayout()
        authLayout.setWidthFull()
        authLayout.justifyContentMode = FlexComponent.JustifyContentMode.END
        val loginLink = Anchor("/login", "Login")
        val signupLink = Anchor("/signup", "Sign Up")
        val dashboardLink = Anchor("/dashboard", "Dashboard")
        authLayout.add(loginLink, signupLink, dashboardLink)
        authLayout.style.setPaddingRight("1rem")
        homeLink.style.setPaddingLeft("1rem")
        addToNavbar(homeLink, authLayout)
    }

    private fun initDrawer() {

    }
}