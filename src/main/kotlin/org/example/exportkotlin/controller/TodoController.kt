package org.example.exportkotlin.controller

import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.dto.todo.req.TodoCreateReqDto
import org.example.exportkotlin.dto.todo.resp.TodoCreateRespDto
import org.example.exportkotlin.dto.todo.resp.TodoFindAllRespDto
import org.example.exportkotlin.dto.todo.resp.TodoFindOneRespDto
import org.example.exportkotlin.service.TodoService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TodoController(
    private val todoService: TodoService
) {
    @PostMapping("/todos")
    fun saveTodo(
        @AuthenticationPrincipal authUser: AuthUser,
        @RequestBody todoCreateReqDto: TodoCreateReqDto
    ): ResponseEntity<TodoCreateRespDto> {
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoCreateReqDto))
    }

    @GetMapping("/todos")
    fun getTodos(
        @RequestParam(defaultValue = "1", required = false, value = "page") page: Int,
        @RequestParam(defaultValue = "10", required = false, value = "size") size: Int,
        @RequestParam(defaultValue = "", required = false, value = "weather") weather: String
    ): ResponseEntity<Page<TodoFindAllRespDto>> {
        return ResponseEntity.ok(todoService.getTodos(page, size, weather))
    }

    @GetMapping("/todos/{todoId}")
    fun getTodo(
        @PathVariable todoId: Long
    ): ResponseEntity<TodoFindOneRespDto> {
        return ResponseEntity.ok(todoService.getTodo(todoId))
    }
}