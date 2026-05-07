package lol.linkstack.view.dashboard.component

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.card.Card
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import lol.linkstack.constants.CssAttribute
import lol.linkstack.constants.CssProperty
import lol.linkstack.constants.CssToken
import lol.linkstack.constants.PageDefaults
import lol.linkstack.service.page.ManagePageService
import lol.linkstack.view.component.ColorPickerDialog

class StyleOptionsComponent(
    private val managePageService: ManagePageService
) : VerticalLayout() {

    private var backgroundColor: String = PageDefaults.BACKGROUND_COLOR
    private var textColor: String = PageDefaults.TEXT_COLOR
    private var cardColor: String = PageDefaults.CARD_COLOR
    private var iconColor: String = PageDefaults.ICON_COLOR
    private var showComments: Boolean = PageDefaults.SHOW_COMMENTS

    private val previewCard = Card().apply {
        width = "100%"
        style.set(CssProperty.PADDING, "1.5rem")
        style.set(CssProperty.TRANSITION, "all 0.3s ease")
        style.set(CssProperty.BORDER_RADIUS, "12px")
    }

    private val showCommentsCheckbox = Checkbox("Show comments on my page")

    private val colorRows = mutableMapOf<ColorField, ColorRow>()

    init {
        isPadding = true
        setWidthFull()
        style.set(CssProperty.MAX_WIDTH, ROOT_MAX_WIDTH)
        style.set(CssProperty.MARGIN, "0 auto")

        showCommentsCheckbox.addValueChangeListener { showComments = it.value }

        val grid = HorizontalLayout(buildSettingsPanel(), buildPreviewPanel()).apply {
            setWidthFull()
            isPadding = false
            isSpacing = true
            alignItems = FlexComponent.Alignment.START
            style.set(CssProperty.FLEX_WRAP, "wrap")
        }

        add(grid)
        loadCurrentStyle()
    }

    private fun buildSettingsPanel(): Component {
        val saveButton = Button("Save changes", Icon(VaadinIcon.CHECK)).apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            addClickListener { saveStyle() }
        }
        val resetButton = Button("Reset", Icon(VaadinIcon.REFRESH)).apply {
            addClickListener { resetToDefaults() }
        }

        val colorsCard = Card().apply {
            style.set(CssProperty.PADDING, "1.5rem")
            style.set(CssProperty.FLEX, PANEL_FLEX)
            style.set(CssProperty.MIN_WIDTH, PANEL_MIN_WIDTH)
            add(
                VerticalLayout(
                    H4("Page colors").apply { style.set(CssProperty.MARGIN, "0 0 1rem 0") },
                    buildColorRow(ColorField.BACKGROUND, "Background"),
                    buildColorRow(ColorField.TEXT, "Text"),
                    buildColorRow(ColorField.CARD, "Card"),
                    buildColorRow(ColorField.ICON, "Icon")
                ).apply {
                    isPadding = false
                    isSpacing = true
                }
            )
        }

        val settingsCard = Card().apply {
            style.set(CssProperty.PADDING, "1.5rem")
            style.set(CssProperty.MARGIN_TOP, "1rem")
            style.set(CssProperty.FLEX, PANEL_FLEX)
            style.set(CssProperty.MIN_WIDTH, PANEL_MIN_WIDTH)
            add(
                VerticalLayout(
                    H4("Settings").apply { style.set(CssProperty.MARGIN, "0 0 1rem 0") },
                    showCommentsCheckbox,
                    HorizontalLayout(saveButton, resetButton).apply {
                        isSpacing = true
                        style.set(CssProperty.MARGIN_TOP, "1rem")
                    }
                ).apply {
                    isPadding = false
                    isSpacing = false
                }
            )
        }

        return VerticalLayout(colorsCard, settingsCard).apply {
            isPadding = false
            style.set(CssProperty.FLEX, OUTER_FLEX)
            style.set(CssProperty.MIN_WIDTH, OUTER_MIN_WIDTH)
        }
    }

    private fun buildPreviewPanel(): Component {
        return Card().apply {
            style.set(CssProperty.PADDING, "1.5rem")
            style.set(CssProperty.FLEX, OUTER_FLEX)
            style.set(CssProperty.MIN_WIDTH, OUTER_MIN_WIDTH)
            add(
                VerticalLayout(
                    H4("Live preview").apply { style.set(CssProperty.MARGIN, "0 0 1rem 0") },
                    previewCard
                ).apply {
                    isPadding = false
                    isSpacing = true
                }
            )
        }
    }

    private fun buildColorRow(field: ColorField, label: String): HorizontalLayout {
        val swatch = Button().apply {
            style.set(CssProperty.WIDTH, SWATCH_SIZE)
            style.set(CssProperty.HEIGHT, SWATCH_SIZE)
            style.set(CssProperty.BORDER_RADIUS, "8px")
            style.set(CssProperty.BORDER, "2px solid ${CssToken.LUMO_CONTRAST_20PCT}")
            style.set(CssProperty.PADDING, "0")
            style.set(CssProperty.MIN_WIDTH, SWATCH_SIZE)
            element.setAttribute(CssAttribute.ARIA_LABEL, "Pick $label color")
        }
        val valueLabel = Span().apply {
            style.set(CssProperty.FONT_FAMILY, CssToken.LUMO_FONT_FAMILY_MONOSPACE)
            style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
            style.set(CssProperty.FONT_SIZE, "0.9rem")
        }

        val row = HorizontalLayout(
            Span(label).apply { style.set(CssProperty.WIDTH, LABEL_WIDTH) },
            swatch,
            valueLabel
        ).apply {
            isPadding = false
            alignItems = FlexComponent.Alignment.CENTER
            isSpacing = true
            setWidthFull()
        }

        val colorRow = ColorRow(swatch, valueLabel)
        colorRows[field] = colorRow
        swatch.addClickListener {
            ColorPickerDialog(getColor(field)) { selected ->
                setColor(field, selected)
                colorRow.update(selected)
                updatePreview()
            }.open()
        }
        return row
    }

    private fun applyColorRows() {
        ColorField.entries.forEach { field ->
            colorRows[field]?.update(getColor(field))
        }
    }

    private fun getColor(field: ColorField): String = when (field) {
        ColorField.BACKGROUND -> backgroundColor
        ColorField.TEXT -> textColor
        ColorField.CARD -> cardColor
        ColorField.ICON -> iconColor
    }

    private fun setColor(field: ColorField, value: String) {
        when (field) {
            ColorField.BACKGROUND -> backgroundColor = value
            ColorField.TEXT -> textColor = value
            ColorField.CARD -> cardColor = value
            ColorField.ICON -> iconColor = value
        }
    }

    private fun updatePreview() {
        previewCard.style.set(CssProperty.BACKGROUND_COLOR, backgroundColor)
        previewCard.style.set(CssProperty.COLOR, textColor)
        previewCard.removeAll()
        previewCard.add(
            H2("Preview User").apply {
                style.set(CssProperty.MARGIN, "0 0 0.5rem 0")
                style.set(CssProperty.COLOR, textColor)
            },
            Span("123 views").apply {
                style.set(CssProperty.BACKGROUND_COLOR, CssToken.LUMO_PRIMARY_COLOR_10PCT)
                style.set(CssProperty.PADDING, "0.25rem 0.75rem")
                style.set(CssProperty.BORDER_RADIUS, "20px")
                style.set(CssProperty.FONT_SIZE, "0.9rem")
                style.set(CssProperty.COLOR, textColor)
            },
            Card().apply {
                style.set(CssProperty.BACKGROUND_COLOR, cardColor)
                style.set(CssProperty.COLOR, textColor)
                style.set(CssProperty.PADDING, "1rem")
                style.set(CssProperty.MARGIN_TOP, "1rem")
                add(
                    HorizontalLayout(
                        Icon(VaadinIcon.LINK).apply {
                            style.set(CssProperty.COLOR, iconColor)
                            style.set(CssProperty.MARGIN_RIGHT, "0.5rem")
                        },
                        Anchor("https://example.com", "Example Link").apply {
                            element.setAttribute(CssAttribute.TARGET, "_blank")
                            element.setAttribute(CssAttribute.REL, CssToken.LINK_REL_NOOPENER)
                            style.set(CssProperty.TEXT_DECORATION, "none")
                            style.set(CssProperty.FONT_WEIGHT, "500")
                            style.set(CssProperty.COLOR, textColor)
                        }
                    ).apply {
                        alignItems = FlexComponent.Alignment.CENTER
                        isPadding = false
                    }
                )
            }
        )
    }

    private fun loadCurrentStyle() {
        try {
            val page = managePageService.getPage()
            backgroundColor = page.backgroundColor
            textColor = page.textColor
            cardColor = page.cardColor
            iconColor = page.iconColor
            showComments = page.showComments
            showCommentsCheckbox.value = showComments
            applyColorRows()
            updatePreview()
        } catch (ex: Exception) {
            showError("Failed to load current style: ${ex.message ?: "unknown error"}")
        }
    }

    private fun resetToDefaults() {
        backgroundColor = PageDefaults.BACKGROUND_COLOR
        textColor = PageDefaults.TEXT_COLOR
        cardColor = PageDefaults.CARD_COLOR
        iconColor = PageDefaults.ICON_COLOR
        showComments = PageDefaults.SHOW_COMMENTS
        showCommentsCheckbox.value = showComments
        applyColorRows()
        updatePreview()
    }

    private fun saveStyle() {
        try {
            managePageService.updatePageStyle(backgroundColor, textColor, cardColor, iconColor, showComments)
            showSuccess("Style updated")
        } catch (ex: Exception) {
            showError("Failed to update style: ${ex.message ?: "unknown error"}")
        }
    }

    private fun showSuccess(msg: String) {
        Notification.show(msg, NOTIFICATION_MS, Notification.Position.TOP_CENTER)
            .addThemeVariants(NotificationVariant.LUMO_SUCCESS)
    }

    private fun showError(msg: String) {
        Notification.show(msg, NOTIFICATION_MS, Notification.Position.TOP_CENTER)
            .addThemeVariants(NotificationVariant.LUMO_ERROR)
    }

    private enum class ColorField { BACKGROUND, TEXT, CARD, ICON }

    private class ColorRow(private val swatch: Button, private val label: Span) {
        fun update(color: String) {
            swatch.style.set(CssProperty.BACKGROUND_COLOR, color)
            label.text = color
        }
    }

    companion object {
        private const val SWATCH_SIZE = "40px"
        private const val LABEL_WIDTH = "100px"
        private const val ROOT_MAX_WIDTH = "1000px"
        private const val NOTIFICATION_MS = 2500
        private const val PANEL_FLEX = "1 1 360px"
        private const val PANEL_MIN_WIDTH = "320px"
        private const val OUTER_FLEX = "1 1 380px"
        private const val OUTER_MIN_WIDTH = "320px"
    }
}
