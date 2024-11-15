package org.example.exportkotlin.dto.comment.resp

import org.example.exportkotlin.domain.comment.Comment

data class CommentFindAllRespDto(
    val commentId: Long,
    val content: String,
    val userId: Long,
    val email: String
) {
    companion object {
        fun of(comment: Comment): CommentFindAllRespDto {
            return CommentFindAllRespDto(
                commentId = comment.id!!,
                content = comment.contents,
                userId = comment.user.id!!,
                email = comment.user.email
            )
        }
    }
}