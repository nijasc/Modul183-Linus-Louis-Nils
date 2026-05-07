package lol.linkstack.view.login

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct
import lol.linkstack.constants.CssProperty
import lol.linkstack.constants.CssToken
import lol.linkstack.constants.Routes

@Route(value = "login", autoLayout = false)
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

        val welcome = H2("Welcome Back!").apply {
            style.set(CssProperty.MARGIN_BOTTOM, "0.5rem")
        }
        val subtitle = Paragraph("Sign in to access your dashboard").apply {
            style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
            style.set(CssProperty.MARGIN_TOP, "0")
        }

        add(welcome, subtitle, loginForm)

        val signupRow = HorizontalLayout().apply {
            isPadding = false
            alignItems = FlexComponent.Alignment.CENTER
            style.set(CssProperty.MARGIN_TOP, "1rem")
        }
        signupRow.add(
            Paragraph("Don't have an account?").apply { style.set(CssProperty.MARGIN, "0") },
            Button("Sign Up", Icon(VaadinIcon.USER)).apply {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
                addClickListener { ui.ifPresent { it.page.setLocation(Routes.SIGNUP) } }
            }
        )
        add(signupRow)
    }

    private fun initLayout() {
        alignItems = FlexComponent.Alignment.CENTER
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        setWidthFull()
        setHeightFull()
        style.set(CssProperty.BACKGROUND, CssToken.GRADIENT_PRIMARY_SECONDARY)
    }

    override fun beforeEnter(event: BeforeEnterEvent) {
        val params = event.location.queryParameters.parameters
        if (params.containsKey(QUERY_ERROR)) loginForm.isError = true
    }

    companion object {
        private const val QUERY_ERROR = "error"
    }
}
