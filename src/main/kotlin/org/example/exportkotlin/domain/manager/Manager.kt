package org.example.exportkotlin.domain.manager

import jakarta.persistence.*
import org.example.exportkotlin.common.entity.BaseEntity
import org.example.exportkotlin.domain.todo.Todo
import org.example.exportkotlin.domain.user.User

@Entity
@Table(name = "managers")
class Manager(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    val todo: Todo,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

) : BaseEntity()