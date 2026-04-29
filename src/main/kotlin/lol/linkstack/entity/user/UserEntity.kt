package lol.linkstack.entity.user

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import lol.linkstack.entity.BaseEntity
import lol.linkstack.entity.page.PageEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
class UserEntity : BaseEntity(), UserDetails {
    var name: String = ""
    var passwordHash: String = ""

    @OneToOne
    @JoinColumn(name = "page_id")
    var page: PageEntity = PageEntity()

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