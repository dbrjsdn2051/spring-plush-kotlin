package org.example.exportkotlin.service

import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.domain.comment.Comment
import org.example.exportkotlin.domain.comment.CommentRepository
import org.example.exportkotlin.domain.todo.TodoRepository
import org.example.exportkotlin.domain.user.User
import org.example.exportkotlin.dto.comment.req.CommentCreateReqDto
import org.example.exportkotlin.dto.comment.resp.CommentCreateRespDto
import org.example.exportkotlin.dto.comment.resp.CommentFindAllRespDto
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.exception.ErrorCode
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val todoRepository: TodoRepository,
    private val commentRepository: CommentRepository,
) {
    fun saveComment(
        authUser: AuthUser,
        todoId: Long,
        dto: CommentCreateReqDto
    ): CommentCreateRespDto {
        val user = User(authUser.getId(), authUser.username, authUser.getRole(), authUser.getNickname())

        val todo = todoRepository.findByIdOrNull(todoId)
            ?: throw CustomApiException(ErrorCode.TODO_NOT_FOUND)

        val comment = Comment(dto.contents, user, todo)

        return commentRepository.save(comment)
            .let { CommentCreateRespDto.of(it, user) }
    }

    fun getComments(todoId: Long): List<CommentFindAllRespDto> {
        return commentRepository.findByTodoIdWithUser(todoId).map { CommentFindAllRespDto.of(it) }
    }
}