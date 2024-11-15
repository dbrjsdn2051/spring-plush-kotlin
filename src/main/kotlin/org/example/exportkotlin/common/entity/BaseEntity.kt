package org.example.exportkotlin.common.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class BaseEntity(

    @CreatedDate
    @Column(updatable = false)
    var createAt: LocalDateTime? = null,

    @LastModifiedDate
    var modifiedAt: LocalDateTime? = null
) {
}