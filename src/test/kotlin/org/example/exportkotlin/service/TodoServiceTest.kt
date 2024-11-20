package org.example.exportkotlin.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.exportkotlin.`client `.WeatherClient
import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.domain.manager.Manager
import org.example.exportkotlin.domain.manager.ManagerRepository
import org.example.exportkotlin.domain.todo.Todo
import org.example.exportkotlin.domain.todo.TodoRepository
import org.example.exportkotlin.dto.todo.req.TodoCreateReqDto
import org.example.exportkotlin.dto.todo.resp.TodoCreateRespDto
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.makeManagerMock
import org.example.exportkotlin.makeTodoMock
import org.example.exportkotlin.makeUserMock
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

class TodoServiceTest : BehaviorSpec({

    val weatherClient = mockk<WeatherClient>()
    val todoRepository = mockk<TodoRepository>()
    val managerRepository = mockk<ManagerRepository>()
    val todoService = TodoService(weatherClient, todoRepository, managerRepository)

    given("일정 저장 시") {
        val authUser = AuthUser(makeUserMock(id = 1L))
        val reqDto = TodoCreateReqDto("title", "content")
        val user = makeUserMock(id = 1L)
        val weather = "sunny"
        val todo = makeTodoMock(id = 1L, weather = weather)
        val manager = makeManagerMock(id = 1L)

        `when`("정상적인 값이 들어왔을 때") {
            every { weatherClient.getTodoWeather() } returns weather
            every { todoRepository.save(any<Todo>()) } returns todo
            every { managerRepository.save(ofType(Manager::class)) } returns manager

            then("정상적으로 수행된다.") {
                val result = todoService.saveTodo(authUser, reqDto)
                result shouldBeEqualToComparingFields TodoCreateRespDto.of(user, todo)
                verify(exactly = 1) { weatherClient.getTodoWeather() }
                verify(exactly = 1) { todoRepository.save(any()) }
                verify(exactly = 1) { managerRepository.save(any()) }
            }
        }
    }

    given("일정 전체 조회 시") {
        val pageable = PageRequest.of(0, 5)
        val weatherSunny = "sunny"
        val weatherRain = "rain"
        val todo1 = makeTodoMock(
            id = 1L,
            weather = weatherSunny,
            user = makeUserMock(id = 1L),
            title = "titleA",
            contents = "contentA"
        )
        val todo2 = makeTodoMock(
            id = 2L,
            weather = weatherRain,
            user = makeUserMock(id = 1L),
            title = "titleB",
            contents = "contentB"
        )

        val todoList = listOf(
            todo1, todo2
        )

        val todoListWithWeather = listOf(todo2)
        val page = PageImpl(todoList, pageable, todoList.size.toLong())
        val pageWithWeather = PageImpl(todoListWithWeather, pageable, todoListWithWeather.size.toLong())

        `when`("날씨 정보를 넣지 않았을 때") {
            every { todoRepository.findAllByOrderByModifiedAtDesc(pageable, null) } returns page

            then("정상 출력된다.") {
                val result = todoService.getTodos(pageable.pageNumber + 1, pageable.pageSize, null)
                result shouldHaveSize 2
                result.content[0].title shouldBe "titleA"
                result.content[0].contents shouldBe "contentA"
                result.content[1].title shouldBe "titleB"
                result.content[1].contents shouldBe "contentB"
                result.content[0].createAt shouldBeBefore LocalDateTime.now()
                result.content[0].modifiedAt shouldBeBefore LocalDateTime.now()
                verify(exactly = 1) { todoRepository.findAllByOrderByModifiedAtDesc(any(), any()) }
            }
        }

        `when`("날씨 정보를 넣었을 때") {
            every { todoRepository.findAllByOrderByModifiedAtDesc(pageable, weatherRain) } returns pageWithWeather

            then("정상 출력된다.") {
                val result = todoService.getTodos(pageable.pageNumber + 1, pageable.pageSize, weatherRain)
                result shouldHaveSize 1
                result.content[0].title shouldBe "titleB"
                result.content[0].contents shouldBe "contentB"
                result.content[0].createAt shouldBeBefore LocalDateTime.now()
                result.content[0].modifiedAt shouldBeBefore LocalDateTime.now()
                verify(exactly = 1) { todoRepository.findAllByOrderByModifiedAtDesc(any(), any()) }
            }
        }
    }

    given("게시글 단건 조회 시") {
        val todoId = 1L
        val todo = makeTodoMock(id = todoId, user = makeUserMock(id = 1L), title = "Test")

        `when`("게시글을 찾을 수 없을 때") {
            every { todoRepository.findByIdWithUser(todoId) } returns null

            then("CustomApiException 이 발생한다.") {
                shouldThrow<CustomApiException> {
                    todoService.getTodo(todoId)
                }.message shouldBe "일정을 찾을 수 없습니다."
                verify(exactly = 1) { todoRepository.findByIdWithUser(any()) }
            }
        }

        `when`("게시글을 찾았을 때") {
            every { todoRepository.findByIdWithUser(todoId) } returns todo

            then("정상 결과를 출력한다.") {
                val result = todoService.getTodo(todoId)
                result.todoId shouldBe 1L
                result.userId shouldBe 1L
                result.title shouldBe "Test"
                verify(exactly = 1) { todoRepository.findByIdWithUser(todoId) }
            }
        }
    }


}) {
    override fun isolationMode() = IsolationMode.InstancePerTest
}