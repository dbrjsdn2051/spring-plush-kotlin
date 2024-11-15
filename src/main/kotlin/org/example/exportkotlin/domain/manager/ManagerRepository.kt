package org.example.exportkotlin.domain.manager

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ManagerRepository : JpaRepository<Manager, Long> {

    @Query("select m from Manager m join fetch m.user where m.todo.id = :todoId")
    fun findByTodoIdWithUser(@Param("todoId") todoId: Long?) : List<Manager>
}