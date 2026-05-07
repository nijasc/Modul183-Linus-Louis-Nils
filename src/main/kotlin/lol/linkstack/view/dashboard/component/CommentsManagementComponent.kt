package lol.linkstack.view.dashboard.component

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.data.renderer.ComponentRenderer
import lol.linkstack.constants.*
import lol.linkstack.dto.page.CommentDto
import lol.linkstack.service.page.CommentService
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CommentsManagementComponent(
    private val commentService: CommentService,
    private val textColor: String = CssToken.LUMO_BODY_TEXT_COLOR,
    private val iconColor: String = CssToken.LUMO_PRIMARY_COLOR
) : VerticalLayout() {

    private val grid = Grid<CommentDto>()
    private val emptyState = Paragraph("No comments yet on your page.").apply {
        style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
    }

    init {
        isPadding = true
        setWidthFull()
        add(
            H4("Comments on your page").apply { style.set(CssProperty.MARGIN_BOTTOM, "0.5rem") }
        )
        setupGrid()
        add(grid, emptyState)
        refreshGrid()
    }

    private fun setupGrid() {
        grid.setWidthFull()
        grid.addColumn { it.content }.setHeader("Content").setFlexGrow(3)
        grid.addColumn { it.authorName }.setHeader("Author").setFlexGrow(1)
        grid.addColumn { formatDate(it.createdAt) }.setHeader("Date").setFlexGrow(1)
        grid.addColumn { "${it.likes}" }.setHeader("Likes").setFlexGrow(0).setWidth(LIKES_COL_WIDTH)
        grid.addColumn(
            ComponentRenderer<HorizontalLayout, CommentDto> { comment -> renderActions(comment) }
        ).setHeader("Actions").setFlexGrow(0).setWidth(ACTIONS_COL_WIDTH)
    }

    private fun renderActions(comment: CommentDto): HorizontalLayout {
        val row = HorizontalLayout().apply { isPadding = false }
        if (comment.isOwnComment) {
            row.add(
                Button(Icon(VaadinIcon.EDIT)) { openEditDialog(comment) }.apply {
                    element.setAttribute(CssAttribute.ARIA_LABEL, "Edit comment")
                    addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY)
                }
            )
        }
        if (comment.canDelete) {
            row.add(
                Button(Icon(VaadinIcon.TRASH)) {
                    runSafely { commentService.deleteComment(comment.id) }
                    showSuccess("Comment deleted")
                    refreshGrid()
                }.apply {
                    element.setAttribute(CssAttribute.ARIA_LABEL, "Delete comment")
                    addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR)
                }
            )
        }
        return row
    }

    private fun refreshGrid() {
        try {
            val comments = commentService.getCommentsOnCurrentUserPage()
            grid.setItems(comments)
            val empty = comments.isEmpty()
            grid.isVisible = !empty
            emptyState.isVisible = empty
        } catch (ex: Exception) {
            showError(ex.message ?: "Failed to load comments")
        }
    }

    private fun openEditDialog(comment: CommentDto) {
        val dialog = Dialog()
        dialog.headerTitle = "Edit comment"

        val textArea = TextArea().apply {
            value = comment.content
            setWidthFull()
            maxLength = ValidationLimits.COMMENT_MAX
            isClearButtonVisible = true
            element.style.set(CssVar.VAADIN_INPUT_FIELD_BACKGROUND, "transparent")
            element.style.set(CssProperty.COLOR, textColor)
            element.style.set("--vaadin-input-field-placeholder-color", textColor)
        }
        val saveButton = Button("Save") {
            try {
                commentService.updateComment(comment.id, textArea.value)
                dialog.close()
                showSuccess("Comment updated")
                refreshGrid()
            } catch (ex: Exception) {
                showError(ex.message ?: "Failed to update comment")
            }
        }.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            style.set(CssProperty.BACKGROUND_COLOR, iconColor)
            style.set(CssProperty.COLOR, CssToken.WHITE)
        }

        dialog.add(
            VerticalLayout(textArea).apply {
                isPadding = false
                setWidth(EDIT_WIDTH)
            }
        )
        dialog.footer.add(Button("Cancel") { dialog.close() }, saveButton)
        dialog.open()
    }

    private fun runSafely(block: () -> Unit) {
        try {
            block()
        } catch (ex: Exception) {
            showError(ex.message ?: "Operation failed")
        }
    }

    private fun showSuccess(msg: String) {
        Notification.show(msg, SUCCESS_MS, Notification.Position.TOP_CENTER)
            .addThemeVariants(NotificationVariant.LUMO_SUCCESS)
    }

    private fun showError(msg: String) {
        Notification.show(msg, ERROR_MS, Notification.Position.TOP_CENTER)
            .addThemeVariants(NotificationVariant.LUMO_ERROR)
    }

    private fun formatDate(instant: Instant): String =
        instant.atZone(ZoneId.systemDefault()).format(DATE_FORMAT)

    companion object {
        private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        private const val EDIT_WIDTH = "400px"
        private const val LIKES_COL_WIDTH = "80px"
        private const val ACTIONS_COL_WIDTH = "130px"
        private const val SUCCESS_MS = 2500
        private const val ERROR_MS = 4000
    }
}
