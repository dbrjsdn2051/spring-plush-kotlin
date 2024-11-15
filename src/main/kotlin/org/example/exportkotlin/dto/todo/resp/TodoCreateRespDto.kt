package org.example.exportkotlin.dto.todo.resp

import org.example.exportkotlin.domain.todo.Todo
import org.example.exportkotlin.domain.user.User
import java.time.LocalDateTime

data class TodoCreateRespDto(
    val todoId: Long,
    val title: String,
    val content: String,
    val weather: String,
    val userId: Long,
    val email: String,
    val createAt: LocalDateTime,
) {
    companion object {
        fun of(user: User, todo: Todo): TodoCreateRespDto {
            return TodoCreateRespDto(
                todoId = todo.id!!,
                title = todo.title,
                content = todo.contents,
                weather = todo.weather,
                userId = user.id!!,
                createAt =  todo.createAt!!,
                email = user.email
            )
        }
    }
}