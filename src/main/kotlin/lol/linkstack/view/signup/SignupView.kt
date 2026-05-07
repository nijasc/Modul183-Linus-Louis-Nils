package lol.linkstack.view.signup

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.card.Card
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct
import jakarta.validation.ConstraintViolationException
import lol.linkstack.constants.CssProperty
import lol.linkstack.constants.CssToken
import lol.linkstack.constants.Routes
import lol.linkstack.dto.SignUpDto
import lol.linkstack.service.user.UserService
import lol.linkstack.view.login.LoginView

@Route(value = "signup", autoLayout = false)
@AnonymousAllowed
class SignupView(
    private val userService: UserService
) : VerticalLayout() {

    private val errorBox = Card().apply {
        isVisible = false
        style.set(CssProperty.BACKGROUND_COLOR, CssToken.LUMO_ERROR_COLOR_10PCT)
        style.set(CssProperty.BORDER, "1px solid ${CssToken.LUMO_ERROR_COLOR}")
        style.set(CssProperty.PADDING, "0.75rem")
        setWidthFull()
    }

    @PostConstruct
    fun init() {
        initLayout()
        add(buildForm())
    }

    private fun initLayout() {
        setWidthFull()
        setHeightFull()
        alignItems = FlexComponent.Alignment.CENTER
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        style.set(CssProperty.BACKGROUND, CssToken.GRADIENT_PRIMARY_SECONDARY)
    }

    private fun buildForm(): Card {
        val usernameField = TextField("Username").apply {
            placeholder = "Choose a username"
            setWidthFull()
            prefixComponent = Icon(VaadinIcon.USER)
        }
        val passwordField = PasswordField("Password").apply {
            placeholder = "Create a strong password"
            setWidthFull()
            prefixComponent = Icon(VaadinIcon.LOCK)
        }
        val signUpButton = Button("Create Account", Icon(VaadinIcon.CHECK)).apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE)
            addClickListener {
                handleSignUp(SignUpDto(usernameField.value, passwordField.value))
            }
        }

        val loginRow = HorizontalLayout(
            Paragraph("Already have an account?").apply { style.set(CssProperty.MARGIN, "0") },
            Button("Log in", Icon(VaadinIcon.SIGN_IN)).apply {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
                addClickListener { ui.ifPresent { it.page.setLocation(Routes.LOGIN) } }
            }
        ).apply {
            isPadding = false
            alignItems = FlexComponent.Alignment.CENTER
        }

        return Card().apply {
            style.set(CssProperty.PADDING, "2rem")
            style.set(CssProperty.BORDER_RADIUS, "8px")
            width = FORM_WIDTH
            add(
                VerticalLayout(
                    H2("Create Your Account").apply { style.set(CssProperty.MARGIN_BOTTOM, "0.5rem") },
                    Paragraph("Join Linkstack and start sharing your links").apply {
                        style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
                        style.set(CssProperty.MARGIN_TOP, "0")
                    },
                    errorBox,
                    usernameField,
                    passwordField,
                    signUpButton,
                    loginRow
                ).apply {
                    isPadding = false
                    isSpacing = true
                    setWidthFull()
                }
            )
        }
    }

    private fun setErrors(errors: List<String>) {
        errorBox.removeAll()
        if (errors.isEmpty()) {
            errorBox.isVisible = false
            return
        }
        errorBox.isVisible = true
        errors.forEach {
            errorBox.add(Paragraph("- $it").apply { style.set(CssProperty.MARGIN, "0.25rem 0") })
        }
    }

    private fun setError(text: String?) {
        if (text.isNullOrBlank()) setErrors(emptyList()) else setErrors(listOf(text))
    }

    private fun handleSignUp(signUp: SignUpDto) {
        setError(null)
        try {
            userService.signUp(signUp)
            UI.getCurrent().navigate(LoginView::class.java)
        } catch (ex: ConstraintViolationException) {
            setErrors(ex.constraintViolations.map { it.message })
        } catch (ex: Exception) {
            setError(ex.message ?: "Sign up failed")
        }
    }

    companion object {
        private const val FORM_WIDTH = "400px"
    }
}
