package lol.linkstack.entity.user

import jakarta.persistence.Entity
import lol.linkstack.entity.BaseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
class UserEntity : BaseEntity(), UserDetails {
    var name: String = ""
    var passwordHash: String = ""

    override fun getAuthorities(): Collection<GrantedAuthority> {
       return listOf(SimpleGrantedAuthority("USER"))
    }

    override fun getPassword(): String? {
        return passwordHash
    }

    override fun getUsername(): String {
        return name
    }
}