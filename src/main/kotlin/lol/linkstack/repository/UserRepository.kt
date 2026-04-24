package lol.linkstack.repository

import lol.linkstack.entity.user.UserEntity
import org.hibernate.validator.constraints.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID>