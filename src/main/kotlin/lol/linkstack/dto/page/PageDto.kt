package lol.linkstack.dto.page

import lol.linkstack.constants.PageDefaults

data class PageDto(
    val links: List<LinkDto>,
    val views: Long,
    val owner: String,
    val backgroundColor: String = PageDefaults.BACKGROUND_COLOR,
    val textColor: String = PageDefaults.TEXT_COLOR,
    val cardColor: String = PageDefaults.CARD_COLOR,
    val iconColor: String = PageDefaults.ICON_COLOR,
    val showComments: Boolean = PageDefaults.SHOW_COMMENTS
)
