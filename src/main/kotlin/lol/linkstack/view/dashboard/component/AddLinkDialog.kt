package lol.linkstack.view.dashboard.component

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import lol.linkstack.constants.*
import lol.linkstack.dto.page.LinkDto

class AddLinkDialog(
    private val onSave: (LinkDto) -> Unit
) : Dialog() {

    private var selectedIcon: VaadinIcon = VaadinIcon.EXTERNAL_LINK
    private val iconDisplay = HorizontalLayout()

    init {
        width = DIALOG_WIDTH
        headerTitle = "Add link"
        build()
    }

    private fun build() {
        val nameField = TextField("Name").apply {
            setWidthFull()
            maxLength = ValidationLimits.LINK_NAME_MAX
            isRequired = true
        }
        val hrefField = TextField("URL").apply {
            setWidthFull()
            maxLength = ValidationLimits.LINK_HREF_MAX
            placeholder = "https://example.com"
            isRequired = true
        }
        val iconColorField = TextField("Icon color").apply {
            value = PageDefaults.ICON_COLOR
            pattern = ValidationLimits.HEX_COLOR_PATTERN
            placeholder = "#000000"
            helperText = "Hex color, e.g. #ff0000"
            setWidthFull()
        }

        styleIconPicker()
        updateIconDisplay()

        iconDisplay.element.addEventListener(CssEvent.CLICK) {
            IconGridPicker { icon ->
                selectedIcon = icon
                updateIconDisplay()
            }.open()
        }

        val changeIconButton = Button("Change icon").apply {
            addClickListener {
                IconGridPicker { icon ->
                    selectedIcon = icon
                    updateIconDisplay()
                }.open()
            }
        }

        val iconRow = HorizontalLayout(iconDisplay, changeIconButton).apply {
            alignItems = FlexComponent.Alignment.CENTER
            setWidthFull()
            isSpacing = true
        }

        val saveButton = Button("Save").apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            addClickListener {
                if (nameField.value.isBlank() || hrefField.value.isBlank()) {
                    return@addClickListener
                }
                val color = iconColorField.value.ifBlank { PageDefaults.ICON_COLOR }
                onSave(
                    LinkDto(
                        href = hrefField.value,
                        name = nameField.value,
                        icon = selectedIcon,
                        iconColor = color
                    )
                )
                close()
            }
        }
        val cancelButton = Button("Cancel") { close() }

        add(
            VerticalLayout(nameField, hrefField, iconRow, iconColorField).apply {
                isPadding = false
                isSpacing = true
                setWidthFull()
            }
        )
        footer.add(cancelButton, saveButton)
    }

    private fun styleIconPicker() {
        iconDisplay.apply {
            alignItems = FlexComponent.Alignment.CENTER
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            style.set(CssProperty.PADDING, "1rem")
            style.set(CssProperty.BORDER, "1px solid ${CssToken.LUMO_CONTRAST_20PCT}")
            style.set(CssProperty.BORDER_RADIUS, "8px")
            style.set(CssProperty.CURSOR, "pointer")
            style.set(CssProperty.MIN_WIDTH, "100px")
            style.set(CssProperty.TRANSITION, "background-color 0.2s")
            element.addEventListener(CssEvent.MOUSE_ENTER) {
                style.set(CssProperty.BACKGROUND_COLOR, CssToken.LUMO_CONTRAST_5PCT)
            }
            element.addEventListener(CssEvent.MOUSE_LEAVE) {
                style.set(CssProperty.BACKGROUND_COLOR, "transparent")
            }
        }
    }

    private fun updateIconDisplay() {
        iconDisplay.removeAll()
        iconDisplay.add(
            Icon(selectedIcon).apply {
                style.set(CssProperty.FONT_SIZE, "2rem")
                style.set(CssProperty.COLOR, CssToken.LUMO_PRIMARY_COLOR)
            }
        )
    }

    companion object {
        private const val DIALOG_WIDTH = "420px"
    }
}
