package lol.linkstack.view.dashboard

import com.vaadin.flow.component.card.Card
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import jakarta.annotation.PostConstruct
import jakarta.annotation.security.PermitAll

@Route("/dashboard")
@PermitAll
class DashboardView : VerticalLayout() {
    @PostConstruct
    fun init() {
        add(Card().apply { add("Hello Authenticated user.") })
    }
}