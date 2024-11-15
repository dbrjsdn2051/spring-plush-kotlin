package org.example.exportkotlin.service

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.domain.manager.Manager
import org.example.exportkotlin.domain.manager.ManagerRepository
import org.example.exportkotlin.domain.todo.TodoRepository
import org.example.exportkotlin.domain.user.UserRepository
import org.example.exportkotlin.dto.manager.req.ManagerCreateReqDto
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.exception.ErrorCode
import org.example.exportkotlin.makeManagerMock
import org.example.exportkotlin.makeTodoMock
import org.example.exportkotlin.makeUserMock
import org.springframework.data.repository.findByIdOrNull

class ManagerServiceTest : BehaviorSpec({

    val managerRepository = mockk<ManagerRepository>()
    val userRepository = mockk<UserRepository>()
    val todoRepository = mockk<TodoRepository>()
    val managerService = ManagerService(managerRepository, userRepository, todoRepository)

    Given("매니저 저장 시") {
        val user = makeUserMock(id = 1L)
        val authUser = AuthUser(user)
        val user2 = makeUserMock(id = 2L)
        val authUser2 = AuthUser(user2)
        val todoId = 1L
        val reqDto = ManagerCreateReqDto(1L)
        val savedSuccessReqDto = ManagerCreateReqDto(2L)
        val todo = makeTodoMock(id = 1L, user = user)
        val managerUser = makeUserMock(id = 2L)
        val manager = makeManagerMock(user = managerUser, todo = todo, id = 1L)

        When("게시글이 없을 때") {
            every { todoRepository.findByIdOrNull(todoId) } returns null

            Then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    managerService.saveManager(authUser, todoId, reqDto)
                    verify(exactly = 1) { todoRepository.findByIdOrNull(todoId) }
                }.message shouldBe "일정을 찾을 수 없습니다."
            }
        }

        When("게시글 작성자와 현재 로그인한 유저와 다를때") {
            every { todoRepository.findByIdOrNull(todoId) } returns todo

            Then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    managerService.saveManager(authUser2, todoId, reqDto)
                    verify(exactly = 1) { todoRepository.findByIdOrNull(todoId) }
                }.message shouldBe ErrorCode.INVALID_TODO_MANAGER.message
            }
        }

        When("담당 매니저를 지정하려고 유저 아이디로 유저를 찾지 못햇을 때") {
            every { todoRepository.findByIdOrNull(todoId) } returns todo
            every { userRepository.findByIdOrNull(reqDto.mangerUserId) } returns null

            Then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    managerService.saveManager(authUser, todoId, reqDto)
                    verify(exactly = 1) { todoRepository.findByIdOrNull(any()) }
                    verify(exactly = 1) { userRepository.findByIdOrNull(any()) }
                }.message shouldBe ErrorCode.USER_NOT_FOUND.message
            }
        }

        When("로그인한 유저가 본인을 담담 매니저로 등록하려고 할떄") {
            every { todoRepository.findByIdOrNull(todoId) } returns todo
            every { userRepository.findByIdOrNull(reqDto.mangerUserId) } returns user

            Then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    managerService.saveManager(authUser, todoId, reqDto)
                    verify(exactly = 1) { todoRepository.findByIdOrNull(any()) }
                    verify(exactly = 1) { userRepository.findByIdOrNull(any()) }
                }.message shouldBe ErrorCode.AUTHOR_CANNOT_ASSIGN_SELF.message
            }
        }

        When("정상적인 값을 넣었을 때") {
            every { todoRepository.findByIdOrNull(todoId) } returns todo
            every { userRepository.findByIdOrNull(savedSuccessReqDto.mangerUserId) } returns managerUser
            every { managerRepository.save(any<Manager>()) } returns manager

            Then("정상적인 값을 출력한다.") {
                val result = managerService.saveManager(authUser, todoId, savedSuccessReqDto)
                result.managerId shouldBe 1L
                result.userId shouldBe 2L
                verify(exactly = 1) { todoRepository.findByIdOrNull(todoId) }
                verify(exactly = 1) { userRepository.findByIdOrNull(savedSuccessReqDto.mangerUserId) }
                verify(exactly = 1) { managerRepository.save(any()) }
            }
        }
    }

    Given("담당자 리스트를 조회할 시") {
        val todoId = 1L
        val todo = makeTodoMock(id = 1L)
        val manger = makeManagerMock(makeUserMock(id = 1L), todo = todo, id = 1L)
        val managerList = listOf(
            makeManagerMock(user = makeUserMock(id = 1L), todo = makeTodoMock(id = 1L), id = 1L),
            makeManagerMock(user = makeUserMock(id = 2L), todo = makeTodoMock(id = 1L), id = 2L)
        )

        When("일정을 찾을 수 없을 때") {
            every { todoRepository.findByIdOrNull(todoId) } returns null
            Then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    managerService.getManagers(todoId)
                    verify(exactly = 1) { todoRepository.findByIdOrNull(any()) }
                }.message shouldBe ErrorCode.TODO_NOT_FOUND.message
            }
        }

        When("정상적인 갑을 넣었을 때") {
            every { todoRepository.findByIdOrNull(todoId) } returns todo
            every { managerRepository.findByTodoIdWithUser(todoId) } returns managerList
            Then("정상적인 값을 출력한다.") {
                val result = managerService.getManagers(todoId)
                result[0].managerId shouldBe 1L
                result[1].managerId shouldBe 2L
                result[0].userId shouldBe 1L
                result[1].userId shouldBe 2L
                verify(exactly = 1) { todoRepository.findByIdOrNull(todoId) }
                verify(exactly = 1) { managerRepository.findByTodoIdWithUser(todoId) }
            }
        }
    }

    Given("담당 매니저를 삭제할 시") {
        val user = makeUserMock(id = 1L)
        val authUser = AuthUser(user)
        val todoId = 1L
        val managerId = 1L
        val todo = makeTodoMock( id = 1L, user = makeUserMock(id = 1L))
        val todoDiff = makeTodoMock(id = 2L, user = makeUserMock(id = 2L))
        val managerDiff = makeManagerMock(user = makeUserMock(id = 1L), todo = makeTodoMock(id = 2L), id = 1L)
        val manager = makeManagerMock(user = user, todo = todo, id = managerId)

        When("게시글을 찾지 못했을 때") {
            every { todoRepository.findByIdOrNull(todoId) } returns null

            Then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    managerService.deleteManager(authUser, todoId, managerId)
                    verify(exactly = 1) { todoRepository.findByIdOrNull(any()) }
                }.message shouldBe ErrorCode.TODO_NOT_FOUND.message
            }
        }

        When("로그인한 유저와 게시글 작성자가 일치 하지 않을 떄") {
            every { todoRepository.findByIdOrNull(todoId) } returns todoDiff

            Then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    managerService.deleteManager(authUser, todoId, managerId)
                    verify(exactly = 1) { todoRepository.findByIdOrNull(any()) }
                }.message shouldBe ErrorCode.INVALID_TODO_USER.message
            }
        }

        When("게시글 담당 매니저와 입력받은 매니저 아디가 다를때") {
            every { todoRepository.findByIdOrNull(todoId) } returns todo
            every { managerRepository.findByIdOrNull(managerId) } returns managerDiff

            Then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    managerService.deleteManager(authUser, todoId, managerId)
                    verify(exactly = 1) { todoRepository.findByIdOrNull(any()) }
                    verify(exactly = 1) { managerRepository.findByIdOrNull(any()) }
                }.message shouldBe ErrorCode.TODO_FORBIDDEN_MANAGER.message
            }
        }

        When("담당 매니저가 없을 때") {
            every { todoRepository.findByIdOrNull(todoId) } returns todo
            every { managerRepository.findByIdOrNull(managerId) } returns null

            Then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    managerService.deleteManager(authUser, todoId, managerId)
                    verify(exactly = 1) { todoRepository.findByIdOrNull(any()) }
                    verify(exactly = 1) { managerRepository.findByIdOrNull(any()) }
                }.message shouldBe ErrorCode.MANAGER_NOT_FOUND.message
            }
        }

        When("정상적인 값이 입력되었을 때") {
            every { todoRepository.findByIdOrNull(todoId) } returns todo
            every { managerRepository.findByIdOrNull(managerId) } returns manager
            every { managerRepository.delete(manager) } returns Unit

            Then("정상적으로 수행된다.") {
                managerService.deleteManager(authUser, todoId, managerId)
                verify(exactly = 1) { todoRepository.findByIdOrNull(todoId) }
                verify(exactly = 1) { managerRepository.findByIdOrNull(managerId) }
                verify(exactly = 1) { managerRepository.delete(any()) }
            }
        }
    }

}) {
    override fun isolationMode() = IsolationMode.InstancePerTest
}
