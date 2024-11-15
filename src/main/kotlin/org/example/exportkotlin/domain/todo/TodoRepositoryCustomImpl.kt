package org.example.exportkotlin.domain.todo

import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.exportkotlin.domain.todo.QTodo.todo
import org.example.exportkotlin.domain.user.QUser.user
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class TodoRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : TodoRepositoryCustom {
    override fun findByIdWithUser(todoId: Long): Todo? {
        return jpaQueryFactory.select(todo)
            .from(todo)
            .leftJoin(todo.user, user)
            .where(todo.id.eq(todoId))
            .fetchOne()
    }

    override fun findAllByOrderByModifiedAtDesc(pageable: Pageable, weather: String?): Page<Todo> {
        val todos = jpaQueryFactory
            .select(todo)
            .from(todo)
            .leftJoin(todo.user).fetchJoin()
            .where(weather?.let { todo.weather.eq(it) })
            .orderBy(todo.modifiedAt.desc())
            .fetch()

        val totalCount = jpaQueryFactory
            .select(todo.count())
            .from(todo)
            .leftJoin(todo.user, user)
            .where(weather?.let { todo.weather.eq(it) })

        return PageableExecutionUtils.getPage(todos, pageable) { totalCount.fetchOne() ?: 0L }
    }
}