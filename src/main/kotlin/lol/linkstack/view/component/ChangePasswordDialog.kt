package lol.linkstack.view.component

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
import com.vaadin.flow.component.textfield.PasswordField
import lol.linkstack.constants.CssProperty
import lol.linkstack.constants.CssToken
import lol.linkstack.service.user.UserService

class ChangePasswordDialog(
    private val userService: UserService
) : Dialog() {

    init {
        headerTitle = "Change Password"
        width = DIALOG_WIDTH
        isCloseOnOutsideClick = false

        val currentPasswordField = PasswordField("Current password").apply {
            setWidthFull()
            prefixComponent = Icon(VaadinIcon.LOCK)
        }

        val newPasswordField = PasswordField("New password").apply {
            setWidthFull()
            prefixComponent = Icon(VaadinIcon.LOCK)
            helperText = "At least 8 characters"
        }

        val confirmPasswordField = PasswordField("Confirm new password").apply {
            setWidthFull()
            prefixComponent = Icon(VaadinIcon.LOCK)
        }

        val errorLabel = Paragraph().apply {
            style.set(CssProperty.COLOR, CssToken.LUMO_ERROR_COLOR)
            style.set(CssProperty.MARGIN, "0")
            isVisible = false
        }

        val saveButton = Button("Change password", Icon(VaadinIcon.CHECK)).apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            addClickListener {
                errorLabel.isVisible = false

                if (newPasswordField.value != confirmPasswordField.value) {
                    errorLabel.text = "New passwords do not match."
                    errorLabel.isVisible = true
                    return@addClickListener
                }

                try {
                    userService.changePassword(currentPasswordField.value, newPasswordField.value)
                    Notification.show("Password changed successfully", NOTIFICATION_MS, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                    close()
                } catch (e: org.springframework.security.access.AccessDeniedException) {
                    errorLabel.text = "Current password is incorrect."
                    errorLabel.isVisible = true
                } catch (e: IllegalArgumentException) {
                    errorLabel.text = e.message ?: "Invalid input."
                    errorLabel.isVisible = true
                }
            }
        }

        val content = VerticalLayout(
            Paragraph("Enter your current password and choose a new one.").apply {
                style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
                style.set(CssProperty.MARGIN, "0")
            },
            currentPasswordField,
            newPasswordField,
            confirmPasswordField,
            errorLabel,
            HorizontalLayout(saveButton, Button("Cancel") { close() }).apply {
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
    }

    companion object {
        private const val DIALOG_WIDTH = "440px"
        private const val NOTIFICATION_MS = 3000
    }
}