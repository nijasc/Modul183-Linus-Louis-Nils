package lol.linkstack.view.dashboard.component

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import lol.linkstack.constants.CssEvent
import lol.linkstack.constants.CssProperty
import lol.linkstack.constants.CssToken

class IconGridPicker(
    private val onIconSelected: (VaadinIcon) -> Unit
) : Dialog() {

    private val grid = Grid<VaadinIcon>()
    private val searchField = TextField("Search icons").apply {
        placeholder = "Type to filter..."
        isClearButtonVisible = true
        addValueChangeListener { filterIcons(it.value) }
        setWidthFull()
    }

    init {
        width = DIALOG_WIDTH
        height = DIALOG_HEIGHT
        headerTitle = "Select an icon"

        setupGrid()
        add(
            VerticalLayout(searchField, grid).apply {
                isPadding = false
                isSpacing = true
                setSizeFull()
            }
        )
        footer.add(
            Button("Cancel") { close() }.apply {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            }
        )
        filterIcons("")
    }

    private fun setupGrid() {
        grid.setWidthFull()
        grid.setHeight(GRID_HEIGHT)
        grid.addComponentColumn { icon ->
            VerticalLayout(
                Icon(icon).apply {
                    style.set(CssProperty.FONT_SIZE, "1.5rem")
                    style.set(CssProperty.COLOR, CssToken.LUMO_PRIMARY_COLOR)
                },
                Span(formatIconName(icon.name)).apply {
                    style.set(CssProperty.FONT_SIZE, "0.75rem")
                    style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
                }
            ).apply {
                alignItems = FlexComponent.Alignment.CENTER
                isPadding = false
                style.set(CssProperty.CURSOR, "pointer")
                element.addEventListener(CssEvent.CLICK) {
                    onIconSelected(icon)
                    close()
                }
            }
        }.setHeader("Icon")
    }

    private fun filterIcons(filter: String?) {
        val items = if (filter.isNullOrBlank()) {
            VaadinIcon.entries.sortedBy { it.name }
        } else {
            val needle = filter.lowercase()
            VaadinIcon.entries
                .filter { it.name.lowercase().contains(needle) }
                .sortedBy { it.name }
        }
        grid.setItems(items)
    }

    private fun formatIconName(name: String): String {
        return name.lowercase().replace("_", " ").split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    companion object {
        private const val DIALOG_WIDTH = "600px"
        private const val DIALOG_HEIGHT = "500px"
        private const val GRID_HEIGHT = "350px"
    }
}
