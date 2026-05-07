package lol.linkstack.repository

import lol.linkstack.entity.page.PageViewEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PageViewRepository : JpaRepository<PageViewEntity, UUID> {
    fun existsByIpAddressAndPageId(ipAddress: String, pageId: UUID): Boolean
    fun countByPageId(pageId: UUID): Long
}
