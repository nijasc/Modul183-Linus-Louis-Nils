package lol.linkstack.view

import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import jakarta.annotation.PostConstruct

@Route("/")
class HomeView : VerticalLayout() {
    @PostConstruct
    fun init() {
        add(Span("Hello"))
    }
}