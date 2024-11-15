package org.example.exportkotlin.service

import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.domain.manager.Manager
import org.example.exportkotlin.domain.manager.ManagerRepository
import org.example.exportkotlin.domain.todo.TodoRepository
import org.example.exportkotlin.domain.user.User
import org.example.exportkotlin.domain.user.UserRepository
import org.example.exportkotlin.dto.manager.req.ManagerCreateReqDto
import org.example.exportkotlin.dto.manager.resp.ManagerCreateRespDto
import org.example.exportkotlin.dto.manager.resp.ManagerFindAllRespDto
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.exception.ErrorCode
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ManagerService(
    private val managerRepository: ManagerRepository,
    private val userRepository: UserRepository,
    private val todoRepository: TodoRepository
) {

    fun saveManager(authUser: AuthUser, todoId: Long, dto: ManagerCreateReqDto): ManagerCreateRespDto {
        val user = User(authUser.getId(), authUser.username, authUser.getRole(), authUser.getNickname())

        val todo = todoRepository.findByIdOrNull(todoId)
            ?: throw CustomApiException(ErrorCode.TODO_NOT_FOUND)

        if (todo.user.id != user.id) {
            throw CustomApiException(ErrorCode.INVALID_TODO_MANAGER)
        }

        val managerUser = userRepository.findByIdOrNull(dto.mangerUserId)
            ?: throw CustomApiException(ErrorCode.USER_NOT_FOUND)

        if (user.id == managerUser.id) {
            throw CustomApiException(ErrorCode.AUTHOR_CANNOT_ASSIGN_SELF)
        }

        return managerRepository.save(Manager(managerUser, todo))
            .let { ManagerCreateRespDto.of(it.id!!, managerUser) }
    }

    fun getManagers(todoId: Long): List<ManagerFindAllRespDto> {
        val todo = (todoRepository.findByIdOrNull(todoId)
            ?: throw CustomApiException(ErrorCode.TODO_NOT_FOUND))

        return managerRepository.findByTodoIdWithUser(todo.id)
            .map { ManagerFindAllRespDto.of(it.id!!, it.user) }
    }

    @Transactional
    fun deleteManager(authUser: AuthUser, todoId: Long, managerId: Long) {
        val user = User(authUser.getId(), authUser.username, authUser.getRole(), authUser.getNickname())
        val todo = todoRepository.findByIdOrNull(todoId)
            ?: throw CustomApiException(ErrorCode.TODO_NOT_FOUND)

        if (user.id != todo.user.id) {
            throw CustomApiException(ErrorCode.INVALID_TODO_USER)
        }

        managerRepository.findByIdOrNull(managerId)
            ?.let {
                if (todo.id != it.todo.id) {
                    throw CustomApiException(ErrorCode.TODO_FORBIDDEN_MANAGER)
                }
                managerRepository.delete(it)
            } ?: throw CustomApiException(ErrorCode.MANAGER_NOT_FOUND)
    }

}