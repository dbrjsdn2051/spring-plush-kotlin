package org.example.exportkotlin.domain.todo

import jakarta.persistence.*
import org.example.exportkotlin.common.entity.BaseEntity
import org.example.exportkotlin.domain.user.User
import java.time.LocalDateTime

@Entity
@Table(name = "todos")
class Todo(

    val title: String,
    val contents: String,
    val weather: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity()