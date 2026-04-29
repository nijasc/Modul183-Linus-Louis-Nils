package lol.linkstack.view.page.component

import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class PageHeaderComponent(username: String, views: Int) : VerticalLayout() {
    init {
        isPadding = false
        defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
        add(
            H2(username).apply { isMargin = false },
            HorizontalLayout(Span("$views views")).apply {
                isPadding = false
                alignItems = FlexComponent.Alignment.CENTER
            }
        )
    }
}
