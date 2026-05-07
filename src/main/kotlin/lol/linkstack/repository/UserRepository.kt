package lol.linkstack.repository

import lol.linkstack.entity.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByNameIgnoreCase(username: String): UserEntity?
    fun existsByNameIgnoreCase(username: String): Boolean
}
