package org.example.exportkotlin.dto.todo.resp

import org.example.exportkotlin.domain.todo.Todo
import org.example.exportkotlin.domain.user.User
import java.time.LocalDateTime

data class TodoFindOneRespDto(
    val todoId: Long,
    val title: String,
    val content: String,
    val weather: String,
    val userId: Long,
    val email: String,
    val createAt: LocalDateTime,
    val modifiedAt: LocalDateTime
) {
    companion object {
        fun of(todo: Todo, user: User): TodoFindOneRespDto {
            return TodoFindOneRespDto(
                todoId = todo.id!!,
                title = todo.title,
                content = todo.contents,
                weather = todo.weather,
                userId = user.id!!,
                email = user.email,
                createAt = todo.createAt!!,
                modifiedAt = todo.modifiedAt!!
            )
        }
    }
}