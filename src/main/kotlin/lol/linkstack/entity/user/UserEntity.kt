package lol.linkstack.entity.user

import jakarta.persistence.Entity
import lol.linkstack.entity.BaseEntity

@Entity
class UserEntity : BaseEntity() {
    var username: String = ""
}