package lol.linkstack.repository

import lol.linkstack.entity.page.LinkEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LinkRepository : JpaRepository<LinkEntity, UUID>
