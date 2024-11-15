package org.example.exportkotlin.domain.comment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {

    @Query("select c from Comment c join fetch c.user where c.todo.id = :todoId")
    fun findByTodoIdWithUser(@Param("todoId") todoId: Long) : List<Comment>
}