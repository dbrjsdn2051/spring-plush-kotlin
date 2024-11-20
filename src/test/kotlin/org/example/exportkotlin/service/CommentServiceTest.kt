package org.example.exportkotlin.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.domain.comment.Comment
import org.example.exportkotlin.domain.comment.CommentRepository
import org.example.exportkotlin.domain.todo.TodoRepository
import org.example.exportkotlin.domain.user.User
import org.example.exportkotlin.domain.user.UserRole
import org.example.exportkotlin.dto.comment.req.CommentCreateReqDto
import org.example.exportkotlin.dto.comment.resp.CommentCreateRespDto
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.makeCommentMock
import org.example.exportkotlin.makeTodoMock
import org.example.exportkotlin.makeUserMock
import org.springframework.data.repository.findByIdOrNull

class CommentServiceTest : BehaviorSpec({

    val commentRepository = mockk<CommentRepository>()
    val todoRepository = mockk<TodoRepository>()
    val commentService = CommentService(todoRepository, commentRepository)

    given("댓글 저장 시") {
        val authUser = AuthUser(
            User(
                id = 1L,
                email = "test@example.com",
                userRole = UserRole.USER,
                nickname = "Spring",
                password = "password"
            )
        )
        val todoId = 1L
        val dto = CommentCreateReqDto("comment")
        val todo = makeTodoMock(id = todoId)
        val user = makeUserMock(id = 1L)
        val comment = makeCommentMock(id = 1L)

        `when`("일정이 존재하지 않을 때") {
            every { todoRepository.findByIdOrNull(todoId) } returns null

            then("CustomApiException 이 발생한다.") {
                shouldThrow<CustomApiException> {
                    commentService.saveComment(authUser, todoId, dto)
                    verify(exactly = 1) { todoRepository.findByIdOrNull(todoId) }
                }.message shouldBe "일정을 찾을 수 없습니다."
            }
        }

        `when`("일정이 존재할 때") {
            every { todoRepository.findByIdOrNull(todoId) } returns todo
            every { commentRepository.save(any<Comment>()) } returns comment

            then("정상적으로 수행된다.") {
                val result = commentService.saveComment(authUser, todoId, dto)
                result shouldBeEqualToComparingFields CommentCreateRespDto.of(comment, user)
                verify(exactly = 1) { todoRepository.findByIdOrNull(todoId) }
            }
        }
    }

    given("댓글 정보들을 불러올 시") {
        val todoId = 1L
        val commentFindAllRespDtos = listOf(
            Comment("content1", makeUserMock(id = 1L), makeTodoMock(id = 1L), 1L),
            Comment("content2", makeUserMock(id = 2L), makeTodoMock(id = 1L), 2L),
            Comment("content3", makeUserMock(id = 3L), makeTodoMock(id = 1L), 3L),
            Comment("content4", makeUserMock(id = 4L), makeTodoMock(id = 1L), 4L),
        )

        `when`("게시글 정보를 찾지 못했을 때") {
            every { commentRepository.findByTodoIdWithUser(todoId) } returns emptyList()

            then("빈 리스트를 반환한다.") {
                val result = commentService.getComments(todoId)
                result shouldHaveSize 0
            }
        }

        `when`("게시글 정보를 찾았을 때") {
            every { commentRepository.findByTodoIdWithUser(todoId) } returns commentFindAllRespDtos

            then("리스트를 반환한다.") {
                val result = commentService.getComments(todoId)
                result shouldHaveSize 4
                verify(exactly = 1) { commentRepository.findByTodoIdWithUser(any()) }
            }
        }
    }

}) {
    override fun isolationMode() = IsolationMode.InstancePerTest
}