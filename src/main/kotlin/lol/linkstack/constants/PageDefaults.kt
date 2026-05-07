package lol.linkstack.constants

object PageDefaults {
    const val BACKGROUND_COLOR = "#ffffff"
    const val TEXT_COLOR = "#000000"
    const val CARD_COLOR = "#f5f5f5"
    const val ICON_COLOR = "#197de1"
    const val SHOW_COMMENTS = true
}

object ValidationLimits {
    const val USERNAME_MIN = 3
    const val USERNAME_MAX = 16
    const val PASSWORD_MIN = 8
    const val PASSWORD_MAX = 256
    const val COMMENT_MAX = 500
    const val LINK_NAME_MAX = 80
    const val LINK_HREF_MAX = 2048
    const val HEX_COLOR_PATTERN = "^#[0-9A-Fa-f]{6}$"
    const val USERNAME_PATTERN = "^[A-Za-z0-9_]+$"
}

object Routes {
    const val HOME = "/"
    const val LOGIN = "/login"
    const val SIGNUP = "/signup"
    const val LOGOUT = "/logout"
    const val DASHBOARD = "/dashboard"
}

object UrlSchemes {
    val ALLOWED = setOf("http", "https")
}
