package lol.linkstack.view.component

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import lol.linkstack.constants.CssProperty
import lol.linkstack.constants.CssToken
import lol.linkstack.constants.JsScripts

class SharePageDialog(private val username: String) : Dialog() {

    init {
        headerTitle = "Share your profile"
        width = DIALOG_WIDTH

        val relativeUrl = buildProfileUrl(username)

        val urlField = TextField("Profile URL").apply {
            value = relativeUrl
            isReadOnly = true
            setWidthFull()
            prefixComponent = Icon(VaadinIcon.LINK)
        }

        UI.getCurrent()?.page
            ?.executeJs(JsScripts.WINDOW_LOCATION_ORIGIN)
            ?.then(String::class.java) { origin -> urlField.value = "$origin$relativeUrl" }

        val copyButton = Button("Copy", Icon(VaadinIcon.COPY)).apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            addClickListener { copyToClipboard(urlField.value) }
        }

        val viewButton = Button("View my profile", Icon(VaadinIcon.EXTERNAL_LINK)).apply {
            addClickListener {
                UI.getCurrent().page.open(relativeUrl, "_blank")
                close()
            }
        }

        val content = VerticalLayout(
            Paragraph("Share this link so others can see your Linkstack page.").apply {
                style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
                style.set(CssProperty.MARGIN, "0")
            },
            urlField,
            HorizontalLayout(copyButton, viewButton).apply {
                justifyContentMode = FlexComponent.JustifyContentMode.END
                isSpacing = true
                setWidthFull()
            }
        ).apply {
            isPadding = false
            isSpacing = true
            setWidth(DIALOG_WIDTH)
        }

        add(content)
        footer.add(Button("Close") { close() })
    }

    private fun buildProfileUrl(username: String): String {
        val handle = if (username.startsWith(USERNAME_PREFIX)) username else "$USERNAME_PREFIX$username"
        return "/$handle"
    }

    private fun copyToClipboard(text: String) {
        UI.getCurrent().page.executeJs(JsScripts.copyToClipboard(text))
        Notification.show("Copied to clipboard", COPY_NOTIFICATION_MS, Notification.Position.TOP_CENTER)
            .addThemeVariants(NotificationVariant.LUMO_SUCCESS)
    }

    companion object {
        private const val DIALOG_WIDTH = "440px"
        private const val COPY_NOTIFICATION_MS = 2000
        private const val USERNAME_PREFIX = "@"
    }
}
