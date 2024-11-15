package org.example.exportkotlin.controller

import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.dto.comment.req.CommentCreateReqDto
import org.example.exportkotlin.dto.comment.resp.CommentCreateRespDto
import org.example.exportkotlin.dto.comment.resp.CommentFindAllRespDto
import org.example.exportkotlin.service.CommentService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping("/todos/{todoId}/comments")
    fun saveComment(
        @AuthenticationPrincipal authUser: AuthUser,
        @PathVariable todoId: Long,
        @RequestBody commentCreateReqDto: CommentCreateReqDto
    ): ResponseEntity<CommentCreateRespDto> {
        return ResponseEntity.ok(commentService.saveComment(authUser, todoId, commentCreateReqDto))
    }

    @GetMapping("/todos/{todoId}/comments")
    fun getComment(
        @PathVariable todoId: Long
    ): ResponseEntity<List<CommentFindAllRespDto>> {
        return ResponseEntity.ok(commentService.getComments(todoId))
    }
}