package lol.linkstack.view.dashboard.component

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import lol.linkstack.dto.page.LinkDto

class AddLinkDialog(
    private val onSave: (LinkDto) -> Unit
) : Dialog() {
    init {
        init()
    }

    private fun init() {

        val nameField = TextField("Name")
        val hrefField = TextField("URL")
        val iconSelect = ComboBox<VaadinIcon>("Icon").apply {
            setItems(*VaadinIcon.entries.toTypedArray())
            setItemLabelGenerator { it.name.lowercase().replace("_", " ") }

            value = VaadinIcon.EXTERNAL_LINK
            setWidthFull()
        }

        add(
            VerticalLayout(
                nameField.apply { setWidthFull() },
                hrefField.apply { setWidthFull() },
                iconSelect,
                HorizontalLayout(
                    Button("Save") {
                        if (nameField.value.isNotBlank() && hrefField.value.isNotBlank()) {
                            onSave(LinkDto(href = hrefField.value, name = nameField.value, icon = iconSelect.value!!))
                            close()
                        }
                    },
                    Button("Cancel") { close() }
                )
            ).apply { setWidthFull() }
        )
        width = "fit-content"
    }
}
