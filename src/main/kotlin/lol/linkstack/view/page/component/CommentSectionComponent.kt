package lol.linkstack.view.page.component

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import lol.linkstack.constants.*
import lol.linkstack.dto.page.CommentDto
import lol.linkstack.service.page.CommentService
import org.springframework.security.core.context.SecurityContextHolder
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CommentSectionComponent(
    private val commentService: CommentService,
    private val pageOwnerUsername: String,
    private val textColor: String,
    private val iconColor: String = CssToken.LUMO_PRIMARY_COLOR,
    private val onCountChanged: (Int) -> Unit = {}
) {

    val listContainer: Div = Div().apply { setWidthFull() }
    val composeContainer: Div = Div().apply {
        addClassName(CssClass.COMPOSE)
        setWidthFull()
    }

    init {
        renderCompose()
        refresh()
    }

    fun refresh() {
        listContainer.removeAll()
        try {
            val comments = commentService.getCommentsForPage(pageOwnerUsername)
            onCountChanged(comments.size)
            if (comments.isEmpty()) {
                listContainer.add(buildEmptyState())
            } else {
                comments.forEach { listContainer.add(buildCommentCard(it)) }
            }
        } catch (ex: Exception) {
            listContainer.add(
                Span(ex.message ?: "Failed to load comments").apply {
                    style.set(CssProperty.COLOR, CssToken.LUMO_ERROR_COLOR)
                }
            )
        }
    }

    private fun renderCompose() {
        composeContainer.removeAll()
        if (!isUserLoggedIn()) {
            composeContainer.add(buildLoginPrompt())
            return
        }
        val textArea = TextArea().apply {
            placeholder = "Share your thoughts..."
            setWidthFull()
            maxLength = ValidationLimits.COMMENT_MAX
            isClearButtonVisible = true
            element.style.set(CssVar.VAADIN_INPUT_FIELD_BACKGROUND, "transparent")
            element.style.set(CssProperty.COLOR, textColor)
            element.style.set("--vaadin-input-field-placeholder-color", textColor)
        }
        val submitButton = Button("Post", Icon(VaadinIcon.PAPERPLANE)).apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            style.set(CssProperty.BACKGROUND_COLOR, iconColor)
            style.set(CssProperty.COLOR, CssToken.WHITE)
            isEnabled = false
        }
        textArea.addValueChangeListener { submitButton.isEnabled = it.value.trim().isNotEmpty() }
        submitButton.addClickListener {
            val value = textArea.value
            if (value.isBlank()) return@addClickListener
            try {
                commentService.addComment(pageOwnerUsername, value)
                textArea.clear()
                showSuccess("Comment posted")
                refresh()
            } catch (ex: Exception) {
                showError(ex.message ?: "Failed to post comment")
            }
        }
        val actionRow = HorizontalLayout(submitButton).apply {
            isPadding = false
            justifyContentMode = FlexComponent.JustifyContentMode.END
            setWidthFull()
        }
        composeContainer.add(
            VerticalLayout(textArea, actionRow).apply {
                isPadding = false
                isSpacing = false
                style.set(CssProperty.GAP, "0.5rem")
            }
        )
    }

    private fun buildLoginPrompt(): Div = Div().apply {
        style.set(CssProperty.TEXT_ALIGN, "center")
        style.set(CssProperty.PADDING, "0.5rem 0")
        add(
            Span("Log in to leave a comment").apply {
                style.set(CssProperty.OPACITY, "0.75")
                style.set(CssProperty.FONT_SIZE, "0.9rem")
            }
        )
        val loginBtn = Button("Log in") {
            ui.ifPresent { it.page.setLocation(Routes.LOGIN) }
        }.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL)
            style.set(CssProperty.MARGIN_TOP, "0.5rem")
            style.set(CssProperty.BACKGROUND_COLOR, iconColor)
            style.set(CssProperty.COLOR, CssToken.WHITE)
        }
        add(Div(loginBtn))
    }

    private fun buildEmptyState(): Div = Div().apply {
        addClassName(CssClass.EMPTY_STATE)
        add(
            Icon(VaadinIcon.COMMENT_O),
            Div(Span("No comments yet")).apply {
                style.set(CssProperty.FONT_WEIGHT, "600")
                style.set(CssProperty.MARGIN_BOTTOM, "0.25rem")
            },
            Span("Be the first to share your thoughts!").apply {
                style.set(CssProperty.FONT_SIZE, "0.85rem")
                style.set(CssProperty.OPACITY, "0.8")
            }
        )
    }

    private fun buildCommentCard(comment: CommentDto): Div {
        val card = Div().apply {
            addClassName(CssClass.COMMENT_CARD)
            style.set(CssProperty.COLOR, textColor)
        }
        val avatar = Span(comment.authorName.firstOrNull()?.uppercaseChar()?.toString() ?: "?").apply {
            addClassName(CssClass.AVATAR)
        }
        val authorName = Span(comment.authorName).apply { addClassName(CssClass.COMMENT_AUTHOR) }
        val dot = Span("·").apply {
            style.set(CssProperty.OPACITY, "0.5")
            style.set(CssProperty.MARGIN, "0 0.35rem")
        }
        val date = Span(formatRelative(comment.createdAt)).apply {
            addClassName(CssClass.COMMENT_DATE)
            element.setAttribute(CssAttribute.TITLE, formatAbsolute(comment.createdAt))
        }
        val authorRow = HorizontalLayout(avatar, authorName, dot, date).apply {
            isPadding = false
            isSpacing = false
            alignItems = FlexComponent.Alignment.CENTER
            style.set(CssProperty.GAP, "0.55rem")
        }
        val header = HorizontalLayout(authorRow).apply {
            setWidthFull()
            isPadding = false
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.CENTER
        }
        renderHeaderActions(header, comment)
        card.add(header, Paragraph(comment.content).apply { addClassName(CssClass.COMMENT_CONTENT) })
        return card
    }

    private fun renderHeaderActions(header: HorizontalLayout, comment: CommentDto) {
        if (isUserLoggedIn()) {
            val actions = HorizontalLayout().apply {
                isPadding = false
                isSpacing = false
                style.set(CssProperty.GAP, "0.15rem")
            }
            actions.add(buildLikeButton(comment))
            if (comment.isOwnComment) actions.add(buildEditButton(comment))
            if (comment.canDelete) actions.add(buildDeleteButton(comment))
            header.add(actions)
        } else if (comment.likes > 0) {
            header.add(buildLikeBadge(comment.likes))
        }
    }

    private fun buildLikeButton(comment: CommentDto): Button {
        val icon = if (comment.hasLiked) VaadinIcon.HEART else VaadinIcon.HEART_O
        return Button("${comment.likes}", Icon(icon)).apply {
            addClassName(CssClass.LIKE_BTN)
            addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY)
            element.setAttribute(CssAttribute.DATA_LIKED, comment.hasLiked.toString())
            element.setAttribute(CssAttribute.ARIA_LABEL, if (comment.hasLiked) "Unlike" else "Like")
            style.set(CssProperty.COLOR, if (comment.hasLiked) iconColor else textColor)
            addClickListener {
                try {
                    commentService.toggleLike(comment.id)
                    refresh()
                } catch (ex: Exception) {
                    showError(ex.message ?: "Failed to like comment")
                }
            }
        }
    }

    private fun buildEditButton(comment: CommentDto): Button {
        return Button(Icon(VaadinIcon.EDIT)).apply {
            addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON)
            element.setAttribute(CssAttribute.ARIA_LABEL, "Edit comment")
            style.set(CssProperty.COLOR, textColor)
            addClickListener { openEditDialog(comment) }
        }
    }

    private fun buildDeleteButton(comment: CommentDto): Button {
        return Button(Icon(VaadinIcon.TRASH)).apply {
            addThemeVariants(
                ButtonVariant.LUMO_SMALL,
                ButtonVariant.LUMO_TERTIARY,
                ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR
            )
            element.setAttribute(CssAttribute.ARIA_LABEL, "Delete comment")
            addClickListener {
                try {
                    commentService.deleteComment(comment.id)
                    showSuccess("Comment deleted")
                    refresh()
                } catch (ex: Exception) {
                    showError(ex.message ?: "Failed to delete comment")
                }
            }
        }
    }

    private fun buildLikeBadge(likes: Int): HorizontalLayout {
        return HorizontalLayout(
            Icon(VaadinIcon.HEART_O).apply {
                style.set(CssProperty.WIDTH, "0.9rem")
                style.set(CssProperty.HEIGHT, "0.9rem")
            },
            Span("$likes").apply { style.set(CssProperty.FONT_SIZE, "0.85rem") }
        ).apply {
            isPadding = false
            isSpacing = false
            alignItems = FlexComponent.Alignment.CENTER
            style.set(CssProperty.GAP, "0.25rem")
            style.set(CssProperty.OPACITY, "0.7")
        }
    }

    private fun openEditDialog(comment: CommentDto) {
        Dialog().apply {
            headerTitle = "Edit comment"
            isCloseOnOutsideClick = false

            val textArea = TextArea().apply {
                value = comment.content
                setWidthFull()
                maxLength = ValidationLimits.COMMENT_MAX
                isClearButtonVisible = true
                element.style.set(CssVar.VAADIN_INPUT_FIELD_BACKGROUND, "transparent")
                element.style.set(CssProperty.COLOR, textColor)
                element.style.set("--vaadin-input-field-placeholder-color", textColor)
            }
            add(Div(textArea).apply { setWidth(EDIT_DIALOG_WIDTH) })
            footer.add(
                Button("Cancel") { close() },
                Button("Save") {
                    try {
                        commentService.updateComment(comment.id, textArea.value)
                        close()
                        showSuccess("Comment updated")
                        refresh()
                    } catch (ex: Exception) {
                        showError(ex.message ?: "Failed to update comment")
                    }
                }.apply {
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    style.set(CssProperty.BACKGROUND_COLOR, iconColor)
                    style.set(CssProperty.COLOR, CssToken.WHITE)
                }
            )
            open()
        }
    }

    private fun formatRelative(instant: Instant): String {
        val seconds = ChronoUnit.SECONDS.between(instant, Instant.now())
        return when {
            seconds < SECONDS_JUST_NOW -> "just now"
            seconds < SECONDS_PER_MINUTE -> "${seconds}s ago"
            seconds < SECONDS_PER_HOUR -> "${seconds / SECONDS_PER_MINUTE}m ago"
            seconds < SECONDS_PER_DAY -> "${seconds / SECONDS_PER_HOUR}h ago"
            seconds < SECONDS_PER_WEEK -> "${seconds / SECONDS_PER_DAY}d ago"
            else -> instant.atZone(ZoneId.systemDefault()).format(DATE_ABS_SHORT)
        }
    }

    private fun formatAbsolute(instant: Instant): String =
        instant.atZone(ZoneId.systemDefault()).format(DATE_ABS_LONG)

    private fun isUserLoggedIn(): Boolean {
        val auth = SecurityContextHolder.getContext().authentication ?: return false
        return auth.isAuthenticated && auth.name != ANONYMOUS
    }

    private fun showSuccess(msg: String) {
        Notification.show(msg, SUCCESS_MS, Notification.Position.TOP_CENTER)
            .addThemeVariants(NotificationVariant.LUMO_SUCCESS)
    }

    private fun showError(msg: String) {
        Notification.show(msg, ERROR_MS, Notification.Position.TOP_CENTER)
            .addThemeVariants(NotificationVariant.LUMO_ERROR)
    }

    companion object {
        private const val ANONYMOUS = "anonymousUser"
        private const val EDIT_DIALOG_WIDTH = "min(420px, 90vw)"
        private const val SUCCESS_MS = 2200
        private const val ERROR_MS = 4000
        private const val SECONDS_JUST_NOW = 5L
        private const val SECONDS_PER_MINUTE = 60L
        private const val SECONDS_PER_HOUR = 3600L
        private const val SECONDS_PER_DAY = 86_400L
        private const val SECONDS_PER_WEEK = 604_800L
        private val DATE_ABS_SHORT: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        private val DATE_ABS_LONG: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm")
    }
}
