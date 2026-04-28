package lol.linkstack.view.login

import com.vaadin.flow.component.card.Card
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import jakarta.annotation.PostConstruct

@Route("/login")
class LoginView: VerticalLayout() {
    @PostConstruct
    fun init() {
        initLayout()
        initForm()
    }

    private fun initForm() {
        val form = Card()
        form.style.setPadding("2rem")
        form.add(Span("Login"))
        form.width = "fit-content"
        add(form)
    }

    private fun initLayout() {
        alignItems = FlexComponent.Alignment.CENTER
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        setWidthFull()
        setHeightFull()
    }
}