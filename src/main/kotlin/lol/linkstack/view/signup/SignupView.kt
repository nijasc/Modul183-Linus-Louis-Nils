package lol.linkstack.view.signup

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.card.Card
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.dom.Style
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct
import jakarta.validation.ConstraintViolationException
import lol.linkstack.dto.SignUpDto
import lol.linkstack.service.user.UserService
import lol.linkstack.view.login.LoginView

@Route("/signup", autoLayout = false)
@AnonymousAllowed
class SignupView(
    private val userService: UserService
) : VerticalLayout() {
    private val layout = VerticalLayout().apply {
        setWidth("fit-content")
    }
    private val errorLayout = Card().apply {
        isVisible = false
        style.setBackground("var(--lumo-error-color-50pct)")
        style.setFlexDirection(Style.FlexDirection.COLUMN)
        setWidthFull()
        add(Icon(VaadinIcon.CLOSE))
        layout.add(this)
    }

    @PostConstruct
    fun init() {
        initLayout()
        initForm()
    }

    private fun initLayout() {
        setWidthFull()
        setHeightFull()
        alignItems = FlexComponent.Alignment.CENTER
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER

        add(layout)
    }

    private fun initForm() {
        val form = Card().apply {
            style.setFlexDirection(Style.FlexDirection.COLUMN)
        }

        form.add(H2("Sign up"))
        val alreadyHaveAccount = HorizontalLayout()
        alreadyHaveAccount.add("Already have an account?")
        val loginAnchor = Anchor("/login", "Login here.")
        alreadyHaveAccount.add(loginAnchor)

        val usernameField = TextField("Username", "Username").apply {
            setWidthFull()
        }
        val passwordField = PasswordField("Password", "Password").apply {
            setWidthFull()
        }
        val signUpButton = Button("Sign Up").apply {
            setWidthFull()
                addClickListener {
                    val signUp = SignUpDto(
                        username = usernameField.value,
                        password = passwordField.value
                    )
                    handleSignUp(signUp)
                }
        }

        form.add(usernameField, passwordField, signUpButton)
        form.add(alreadyHaveAccount)
        layout.add(form)
    }

    private fun setError(text: String?) {
        errorLayout.removeAll()
        if (text.isNullOrBlank()) {
            errorLayout.isVisible = false
            return
        }
        errorLayout.isVisible = true
        errorLayout.add(text)
    }

    private fun setErrors(errors: List<String>) {
        if (errors.isEmpty()) {
            setError(null)
            return
        }
        if (errors.size == 1) {
            setError(errors[0])
            return
        }
        errorLayout.removeAll()
        errorLayout.isVisible = true
        errors.forEach {
            errorLayout.add(Paragraph("- $it"))
        }
    }

    private fun handleSignUp(signUp: SignUpDto) {
        setError(null)
        try {
            userService.signUp(signUp)
            UI.getCurrent().navigate(LoginView::class.java)
        } catch (e: Exception) {
            if (e is ConstraintViolationException) {
                val errorMessages = e.constraintViolations.map { it.message }
                setErrors(errorMessages)
                return
            }
           setError(e.message)
        }
    }
}