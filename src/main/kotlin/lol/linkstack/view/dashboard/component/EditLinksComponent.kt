package lol.linkstack.view.dashboard.component

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import lol.linkstack.dto.page.LinkDto
import lol.linkstack.service.page.ManagePageService

class EditLinksComponent(
    private val managePageService: ManagePageService
) : VerticalLayout() {

    private val grid = Grid<LinkDto>()

    init {
        init()
    }

    fun init() {
        isPadding = false
        grid.setWidthFull()
        grid.addColumn { it.name }.setHeader("Name")
        grid.addColumn { it.href }.setHeader("URL")
        grid.addColumn(ComponentRenderer { link -> Icon(link.icon) }).setHeader("Icon")
        grid.addColumn(
            ComponentRenderer { link ->
                Button(VaadinIcon.TRASH.create()) {
                    managePageService.removeLink(link.id)
                    refreshGrid()
                }.apply { style.set("color", "var(--lumo-error-color-50pct)") }
            }
        ).setHeader("Actions")

        add(
            grid,
            Button(VaadinIcon.PLUS_CIRCLE.create()) { openAddDialog() }
        )
        refreshGrid()
    }

    private fun refreshGrid() {
        grid.setItems(managePageService.getLinks())
    }

    private fun openAddDialog() {
        AddLinkDialog { link ->
            managePageService.addLink(link)
            refreshGrid()
        }.open()
    }
}