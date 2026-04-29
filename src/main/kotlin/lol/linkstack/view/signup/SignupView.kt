package lol.linkstack.view.signup

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct

@Route("/signup", autoLayout = false)
@AnonymousAllowed
class SignupView : VerticalLayout() {
    @PostConstruct
    fun init() {

    }
}