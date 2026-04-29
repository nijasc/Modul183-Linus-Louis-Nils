package lol.linkstack.repository

import lol.linkstack.entity.page.PageEntity
import org.hibernate.validator.constraints.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PageRepository : JpaRepository<PageEntity, UUID> {
}