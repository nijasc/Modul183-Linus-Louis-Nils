package lol.linkstack.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.util.*

@MappedSuperclass
class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    @Column(name = "updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now()

    @CreatedDate
    @Column(name = "created_at")
    var createdAt: Instant = Instant.now()
}