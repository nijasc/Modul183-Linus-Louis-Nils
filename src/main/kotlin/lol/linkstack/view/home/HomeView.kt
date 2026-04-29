package lol.linkstack.view.home

import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct

@Route("/")
@AnonymousAllowed
class HomeView : VerticalLayout() {
    @PostConstruct
    fun init() {
        add(Span("Hello"))
    }
}