package lol.linkstack.view.component

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import lol.linkstack.constants.CssAttribute
import lol.linkstack.constants.CssProperty

class ColorPickerDialog(
    private val currentColor: String,
    private val onColorSelected: (String) -> Unit
) : Dialog() {

    init {
        width = DIALOG_WIDTH
        headerTitle = "Choose a color"
        add(buildPalette())
    }

    private fun buildPalette(): VerticalLayout {
        val grid = VerticalLayout().apply {
            isPadding = false
            isSpacing = false
        }
        PRESET_COLORS.chunked(COLUMNS).forEach { rowColors ->
            val row = HorizontalLayout().apply {
                isPadding = false
                isSpacing = true
            }
            rowColors.forEach { color -> row.add(buildSwatch(color)) }
            grid.add(row)
        }
        return grid
    }

    private fun buildSwatch(color: String): Button {
        val isSelected = color.equals(currentColor, ignoreCase = true)
        val borderColor = if (isSelected) BORDER_SELECTED else BORDER_DEFAULT
        return Button().apply {
            style.set(CssProperty.BACKGROUND_COLOR, color)
            style.set(CssProperty.WIDTH, SWATCH_SIZE)
            style.set(CssProperty.HEIGHT, SWATCH_SIZE)
            style.set(CssProperty.BORDER, "2px solid $borderColor")
            style.set(CssProperty.CURSOR, "pointer")
            style.set(CssProperty.BORDER_RADIUS, "4px")
            style.set(CssProperty.PADDING, "0")
            style.set(CssProperty.MIN_WIDTH, SWATCH_SIZE)
            element.setAttribute(CssAttribute.ARIA_LABEL, "Color $color")
            addClickListener {
                onColorSelected(color)
                close()
            }
        }
    }

    companion object {
        private const val DIALOG_WIDTH = "320px"
        private const val SWATCH_SIZE = "40px"
        private const val COLUMNS = 4
        private const val BORDER_SELECTED = "#000"
        private const val BORDER_DEFAULT = "#ccc"
        private val PRESET_COLORS = listOf(
            "#ffffff", "#f8f9fa", "#e9ecef", "#dee2e6",
            "#000000", "#343a40", "#495057", "#6c757d",
            "#197de1", "#1862a8", "#0d47a1", "#01579b",
            "#dc3545", "#c82333", "#bd2130", "#a71d2a",
            "#28a745", "#218838", "#1e7e34", "#155724",
            "#ffc107", "#e0a800", "#d39e00", "#b38b00",
            "#17a2b8", "#138496", "#117a8b", "#0c525d"
        )
    }
}
