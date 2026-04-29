package lol.linkstack.dto.page

data class PageDto(
    val links: List<LinkDto>,
    val views: Int,
    val owner: String
)