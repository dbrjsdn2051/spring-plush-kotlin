package org.example.exportkotlin

import org.example.exportkotlin.domain.comment.Comment
import org.example.exportkotlin.domain.manager.Manager
import org.example.exportkotlin.domain.todo.Todo
import org.example.exportkotlin.domain.user.User
import org.example.exportkotlin.domain.user.UserRole
import java.time.LocalDateTime

fun makeUserMock(
    email: String = "test@example.com",
    password: String = "password",
    userRole: UserRole = UserRole.USER,
    nickname: String = "spring",
    id: Long? = null
): User {
    return User(
        email = email,
        password = password,
        userRole = userRole,
        nickname = nickname,
        id = id
    )
}


fun makeTodoMock(
    title: String = "Test Title",
    contents: String = "Test Content",
    weather: String = "Sunny",
    user: User = makeUserMock(),
    id: Long? = null
): Todo {
    return Todo(
        title = title,
        contents = contents,
        weather = weather,
        user = user,
        id = id,
    ).also {
        it.createAt = LocalDateTime.now()
        it.modifiedAt = LocalDateTime.now()
    }
}

fun makeManagerMock(
    user: User = makeUserMock(),
    todo: Todo = makeTodoMock(),
    id: Long? = null,
): Manager {
    return Manager(
        user = user,
        todo = todo,
        id = id
    )
}


fun makeCommentMock(
    contents: String = "content",
    user: User = makeUserMock(),
    todo: Todo = makeTodoMock(),
    id: Long? = null
): Comment {
    return Comment(
        contents = contents,
        user = user,
        todo = todo,
        id = id
    )
}