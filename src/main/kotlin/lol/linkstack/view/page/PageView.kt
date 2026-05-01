package lol.linkstack.view.page

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import lol.linkstack.dto.page.PageDto
import lol.linkstack.service.page.PageService
import lol.linkstack.view.page.component.PageHeaderComponent
import lol.linkstack.view.page.component.PageLinksComponent

@Route("/")
@AnonymousAllowed
class PageView(
    private val pageService: PageService
) : VerticalLayout(), HasUrlParameter<String>, HasDynamicTitle {
    var username = ""
    init {
        isPadding = false
        isSpacing = false
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
    }

    override fun setParameter(before: BeforeEvent, value: String) {
        if (!pageService.pageExists(value)) {
            showNotFound(value)
            return
        }
        username = value
        val page = pageService.getPageAndIncrementViews(value)
        renderPage(page)
    }

    private fun renderPage(page: PageDto) {
        removeAll()
        add(PageHeaderComponent(page.owner, page.views))
        add(PageLinksComponent(page.links))
    }

    private fun showNotFound(username: String) {
        removeAll()
        add(
            Div("User '$username' not found")
                .apply { style.set("color", "var(--lumo-error-color-50pct)") })
    }

    override fun getPageTitle(): String {
       return username
    }
}