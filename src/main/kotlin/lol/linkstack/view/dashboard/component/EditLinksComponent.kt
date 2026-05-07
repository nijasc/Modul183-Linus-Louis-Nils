package lol.linkstack.view.dashboard.component

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import lol.linkstack.constants.CssAttribute
import lol.linkstack.constants.CssProperty
import lol.linkstack.dto.page.LinkDto
import lol.linkstack.service.page.ManagePageService

class EditLinksComponent(
    private val managePageService: ManagePageService
) : VerticalLayout() {

    private val grid = Grid<LinkDto>()

    init {
        isPadding = true
        setWidthFull()
        setupGrid()

        val addButton = Button("Add Link", Icon(VaadinIcon.PLUS_CIRCLE)).apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            addClickListener { openAddDialog() }
        }
        add(
            HorizontalLayout(addButton).apply {
                isPadding = false
                style.set(CssProperty.MARGIN_BOTTOM, "0.5rem")
            },
            grid
        )
        refreshGrid()
    }

    private fun setupGrid() {
        grid.setWidthFull()
        grid.addColumn { it.name }.setHeader("Name").setFlexGrow(1)
        grid.addColumn { it.href }.setHeader("URL").setFlexGrow(2)
        grid.addColumn(
            ComponentRenderer { link ->
                Icon(link.icon).apply { style.set(CssProperty.COLOR, link.iconColor) }
            }
        ).setHeader("Icon").setFlexGrow(0).setWidth(ICON_COL_WIDTH)
        grid.addColumn(
            ComponentRenderer { link ->
                Button(VaadinIcon.TRASH.create()) {
                    runSafely { managePageService.removeLink(link.id) }
                    refreshGrid()
                }.apply {
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_ICON)
                    element.setAttribute(CssAttribute.ARIA_LABEL, "Delete link")
                }
            }
        ).setHeader("Actions").setFlexGrow(0).setWidth(ACTIONS_COL_WIDTH)
    }

    private fun refreshGrid() {
        try {
            grid.setItems(managePageService.getLinks())
        } catch (ex: Exception) {
            showError("Failed to load links: ${ex.message ?: "unknown error"}")
        }
    }

    private fun openAddDialog() {
        AddLinkDialog { link ->
            try {
                managePageService.addLink(link)
                refreshGrid()
            } catch (ex: Exception) {
                showError(ex.message ?: "Failed to add link")
            }
        }.open()
    }

    private fun runSafely(block: () -> Unit) {
        try {
            block()
        } catch (ex: Exception) {
            showError(ex.message ?: "Operation failed")
        }
    }

    private fun showError(msg: String) {
        Notification.show(msg, NOTIFICATION_MS, Notification.Position.TOP_CENTER)
            .addThemeVariants(NotificationVariant.LUMO_ERROR)
    }

    companion object {
        private const val NOTIFICATION_MS = 4000
        private const val ICON_COL_WIDTH = "80px"
        private const val ACTIONS_COL_WIDTH = "100px"
    }
}
