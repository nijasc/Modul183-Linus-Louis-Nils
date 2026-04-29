package lol.linkstack.view.page

import com.vaadin.flow.component.card.Card
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct
import lol.linkstack.dto.page.PageDto
import lol.linkstack.service.page.PageService

@Route("/")
@AnonymousAllowed
class PageView(
    private val pageService: PageService
) : VerticalLayout(), HasUrlParameter<String> {
    private val userExistsParagraph = Paragraph()

    @PostConstruct
    fun init() {
        add(userExistsParagraph)
    }

    override fun setParameter(before: BeforeEvent, value: String) {
        val exists = pageService.pageExists(value)
        userExistsParagraph.text = "User exists: $exists"
        if (exists) {
            val page = pageService.getPage(value)
            init(page)
        }
    }

    private fun init(page: PageDto) {
        add("Views: ${page.views}")
        page.links.forEach {
            val card = Card()
            card.add(Icon(it.icon))
            card.add(Anchor(it.href, it.name))

        }
        add("Page: ${page.owner}")
    }
}