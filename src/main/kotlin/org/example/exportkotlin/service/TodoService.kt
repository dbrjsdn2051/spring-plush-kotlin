package org.example.exportkotlin.service

import org.example.exportkotlin.client.WeatherClient
import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.domain.manager.Manager
import org.example.exportkotlin.domain.manager.ManagerRepository
import org.example.exportkotlin.domain.todo.Todo
import org.example.exportkotlin.domain.todo.TodoRepository
import org.example.exportkotlin.domain.user.User
import org.example.exportkotlin.dto.todo.req.TodoCreateReqDto
import org.example.exportkotlin.dto.todo.resp.TodoCreateRespDto
import org.example.exportkotlin.dto.todo.resp.TodoFindAllRespDto
import org.example.exportkotlin.dto.todo.resp.TodoFindOneRespDto
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.exception.ErrorCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class TodoService(
    private val weatherClient: WeatherClient,
    private val todoRepository: TodoRepository,
    private val managerRepository: ManagerRepository,
) {

    fun saveTodo(authUser: AuthUser, dto: TodoCreateReqDto): TodoCreateRespDto {
        val user = User(authUser.getId(), authUser.username, authUser.getRole(), authUser.getNickname())
        val weather = weatherClient.getTodoWeather()
        val todo = Todo(dto.title, dto.contents, weather, user)
        val savedTodo = todoRepository.save(todo)
        managerRepository.save(Manager(user, todo))

        return TodoCreateRespDto.of(user, savedTodo)
    }

    fun getTodos(page: Int, size: Int, weather: String?): Page<TodoFindAllRespDto> {
        val pageable = PageRequest.of(page - 1, size)
        return todoRepository.findAllByOrderByModifiedAtDesc(pageable, weather)
            .map { TodoFindAllRespDto.of(it, it.user) }
    }

    fun getTodo(todoId: Long): TodoFindOneRespDto {
        return todoRepository.findByIdWithUser(todoId)
            ?.let { TodoFindOneRespDto.of(it, it.user) }
            ?: throw CustomApiException(ErrorCode.TODO_NOT_FOUND)
    }
}