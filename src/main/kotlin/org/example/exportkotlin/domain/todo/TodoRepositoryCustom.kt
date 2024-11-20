package org.example.exportkotlin.domain.todo

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TodoRepositoryCustom {

    fun findByIdWithUser(todoId: Long): Todo?

    @Transactional(readOnly = true)
    fun findAllByOrderByModifiedAtDesc(pageable: Pageable, weather: String?): Page<Todo>
}