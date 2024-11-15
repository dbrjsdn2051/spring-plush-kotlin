package org.example.exportkotlin.controller

import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.dto.user.req.UserChangePasswordReqDto
import org.example.exportkotlin.dto.user.req.UserCreateRepDto
import org.example.exportkotlin.dto.user.resp.UserCreateRespDto
import org.example.exportkotlin.dto.user.resp.UserFindOneRespDto
import org.example.exportkotlin.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
) {
    @PostMapping("/auth/register")
    fun register(@RequestBody userCreateRepDto: UserCreateRepDto): ResponseEntity<UserCreateRespDto> {
        return ResponseEntity.ok(userService.addUser(userCreateRepDto))
    }

    @GetMapping("/users/{userId}")
    fun getUser(
        @PathVariable userId: Long
    ): ResponseEntity<UserFindOneRespDto> {
        return ResponseEntity.ok(userService.getUser(userId))
    }

    @PutMapping("/users")
    fun changePassword(
        @AuthenticationPrincipal authUser: AuthUser,
        @RequestBody userChangePasswordReqDto: UserChangePasswordReqDto
    ) {
        userService.changePassword(authUser.getId(), userChangePasswordReqDto)
    }
}