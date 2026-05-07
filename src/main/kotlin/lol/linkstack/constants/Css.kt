package lol.linkstack.constants

object CssClass {
    const val PAGE_ROOT = "ls-page-root"
    const val PAGE_CONTENT = "ls-page-content"
    const val COMMENTS_FAB = "ls-comments-fab"
    const val COMMENT_DRAWER = "ls-comment-drawer"
    const val DRAWER_BACKDROP = "ls-drawer-backdrop"
    const val DRAWER_HEADER = "ls-drawer-header"
    const val DRAWER_BODY = "ls-drawer-body"
    const val DRAWER_FOOTER = "ls-drawer-footer"
    const val DRAWER_TITLE = "ls-drawer-title"
    const val DRAWER_COUNT = "ls-drawer-count"
    const val COMPOSE = "ls-compose"
    const val EMPTY_STATE = "ls-empty-state"
    const val COMMENT_CARD = "ls-comment-card"
    const val COMMENT_AUTHOR = "ls-comment-author"
    const val COMMENT_DATE = "ls-comment-date"
    const val COMMENT_CONTENT = "ls-comment-content"
    const val AVATAR = "ls-avatar"
    const val LIKE_BTN = "ls-like-btn"
    const val VIEWS_PILL = "ls-views-pill"
}

object CssVar {
    const val BG_COLOR = "--ls-bg-color"
    const val TEXT_COLOR = "--ls-text-color"
    const val CARD_COLOR = "--ls-card-color"
    const val ICON_COLOR = "--ls-icon-color"
    const val VAADIN_INPUT_FIELD_BACKGROUND = "--vaadin-input-field-background"
}

object CssProperty {
    const val BACKGROUND_COLOR = "background-color"
    const val COLOR = "color"
    const val MARGIN = "margin"
    const val MARGIN_TOP = "margin-top"
    const val MARGIN_BOTTOM = "margin-bottom"
    const val MARGIN_LEFT = "margin-left"
    const val MARGIN_RIGHT = "margin-right"
    const val PADDING = "padding"
    const val WIDTH = "width"
    const val HEIGHT = "height"
    const val MIN_WIDTH = "min-width"
    const val MAX_WIDTH = "max-width"
    const val FONT_SIZE = "font-size"
    const val FONT_WEIGHT = "font-weight"
    const val TEXT_ALIGN = "text-align"
    const val TEXT_DECORATION = "text-decoration"
    const val BORDER = "border"
    const val BORDER_RADIUS = "border-radius"
    const val BOX_SHADOW = "box-shadow"
    const val CURSOR = "cursor"
    const val DISPLAY = "display"
    const val FLEX = "flex"
    const val FLEX_WRAP = "flex-wrap"
    const val GAP = "gap"
    const val OPACITY = "opacity"
    const val TRANSFORM = "transform"
    const val TRANSITION = "transition"
    const val ANIMATION = "animation"
    const val LINE_HEIGHT = "line-height"
    const val FONT_FAMILY = "font-family"
    const val MIN_HEIGHT = "min-height"
    const val BACKGROUND = "background"
    const val JUSTIFY_CONTENT = "justify-content"
    const val ALIGN_ITEMS = "align-items"
}

object CssAttribute {
    const val STYLE = "style"
    const val ARIA_LABEL = "aria-label"
    const val TITLE = "title"
    const val TARGET = "target"
    const val REL = "rel"
    const val DATA_OPEN = "data-open"
    const val DATA_LIKED = "data-liked"
}

object CssEvent {
    const val CLICK = "click"
    const val MOUSE_ENTER = "mouseenter"
    const val MOUSE_LEAVE = "mouseleave"
}

object CssToken {
    const val LUMO_PRIMARY_COLOR = "var(--lumo-primary-color)"
    const val LUMO_PRIMARY_COLOR_10PCT = "var(--lumo-primary-color-10pct)"
    const val LUMO_SECONDARY_COLOR_10PCT = "var(--lumo-secondary-color-10pct)"
    const val LUMO_SECONDARY_TEXT_COLOR = "var(--lumo-secondary-text-color)"
    const val LUMO_BODY_TEXT_COLOR = "var(--lumo-body-text-color)"
    const val LUMO_BASE_COLOR = "var(--lumo-base-color)"
    const val LUMO_CONTRAST_5PCT = "var(--lumo-contrast-5pct)"
    const val LUMO_CONTRAST_10PCT = "var(--lumo-contrast-10pct)"
    const val LUMO_CONTRAST_20PCT = "var(--lumo-contrast-20pct)"
    const val LUMO_ERROR_COLOR = "var(--lumo-error-color)"
    const val LUMO_ERROR_COLOR_10PCT = "var(--lumo-error-color-10pct)"
    const val LUMO_SUCCESS_COLOR = "var(--lumo-success-color)"
    const val LUMO_FONT_FAMILY_MONOSPACE = "var(--lumo-font-family-monospace, monospace)"

    const val GRADIENT_PRIMARY_SECONDARY =
        "linear-gradient(135deg, var(--lumo-primary-color-10pct), var(--lumo-secondary-color-10pct))"

    const val FADE_IN_ANIMATION = "ls-fade-in 0.5s ease both"
    const val FADE_IN_ANIMATION_LONG = "ls-fade-in 0.6s ease both"
    const val FADE_IN_ANIMATION_SHORT = "ls-fade-in 0.4s ease both"

    const val WHITE = "#fff"
    const val SHADOW_LIGHT = "0 2px 4px rgba(0,0,0,0.1)"
    const val SHADOW_MEDIUM = "0 4px 12px rgba(0,0,0,0.15)"
    const val SHADOW_HOVER = "0 8px 16px rgba(0,0,0,0.15)"
    const val SHADOW_CARD = "0 8px 24px rgba(0,0,0,0.08)"

    const val LINK_REL_NOOPENER = "noopener noreferrer nofollow"
}
