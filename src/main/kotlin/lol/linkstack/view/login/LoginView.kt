package lol.linkstack.view.login

import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct

@Route("/login", autoLayout = false)
@AnonymousAllowed
class LoginView : VerticalLayout(), BeforeEnterObserver {
    private val loginForm = LoginForm()

    @PostConstruct
    fun init() {
        initLayout()
        initForm()
    }

    private fun initForm() {
        loginForm.action = "login"
        loginForm.isForgotPasswordButtonVisible = false

        add(loginForm)
        val noAccount = HorizontalLayout()
        val signupAnchor = Anchor("/signup", "Create one here.")
        noAccount.add("Don't have an account?")
        noAccount.add(signupAnchor)
        add(noAccount)
    }

    private fun initLayout() {
        alignItems = FlexComponent.Alignment.CENTER
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        setWidthFull()
        setHeightFull()
    }

    override fun beforeEnter(event: BeforeEnterEvent) {
        val hasError = event.location.queryParameters.parameters.containsKey("error")
        if (hasError) {
            loginForm.isError = true
        }
    }
}