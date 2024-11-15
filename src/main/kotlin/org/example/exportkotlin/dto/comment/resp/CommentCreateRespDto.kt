package org.example.exportkotlin.dto.comment.resp

import org.example.exportkotlin.domain.comment.Comment
import org.example.exportkotlin.domain.user.User

data class CommentCreateRespDto(
    val commentId: Long,
    val content: String,
    val userId: Long,
    val email: String
) {
    companion object {
        fun of(comment: Comment, user: User): CommentCreateRespDto {
            return CommentCreateRespDto(
                commentId = comment.id!!,
                content = comment.contents,
                userId = user.id!!,
                email = user.email,
            )
        }
    }
}