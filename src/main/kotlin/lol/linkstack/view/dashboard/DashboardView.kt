package lol.linkstack.view.dashboard

import com.vaadin.flow.component.card.Card
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import jakarta.annotation.PostConstruct
import jakarta.annotation.security.PermitAll
import lol.linkstack.service.user.UserService
import lol.linkstack.view.dashboard.component.EditLinksComponent

@Route("/dashboard")
@PermitAll
class DashboardView(
    private val userService: UserService
) : VerticalLayout() {
    @PostConstruct
    fun init() {
        val username = userService.getProfile()
        add(Card().apply { add("Hello Authenticated user. ($username)") })
        add(EditLinksComponent())
    }
}