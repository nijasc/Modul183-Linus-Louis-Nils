package lol.linkstack.repository

import lol.linkstack.entity.page.PageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PageRepository : JpaRepository<PageEntity, UUID>
