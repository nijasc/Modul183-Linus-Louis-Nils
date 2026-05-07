package lol.linkstack.repository

import lol.linkstack.entity.page.LinkEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LinkRepository : JpaRepository<LinkEntity, UUID> {
    fun findByPageId(pageId: UUID): List<LinkEntity>
}
