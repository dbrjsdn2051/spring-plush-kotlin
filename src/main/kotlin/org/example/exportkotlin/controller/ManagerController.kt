package org.example.exportkotlin.controller

import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.dto.manager.req.ManagerCreateReqDto
import org.example.exportkotlin.dto.manager.resp.ManagerCreateRespDto
import org.example.exportkotlin.dto.manager.resp.ManagerFindAllRespDto
import org.example.exportkotlin.service.ManagerService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ManagerController(
    private val managerService: ManagerService
) {

    @PostMapping("/todos/{todoId}/managers")
    fun saveManager(
        @AuthenticationPrincipal authUser: AuthUser,
        @PathVariable todoId: Long,
        @RequestBody managerCreateReqDto: ManagerCreateReqDto
    ): ResponseEntity<ManagerCreateRespDto> {
        return ResponseEntity.ok(managerService.saveManager(authUser, todoId, managerCreateReqDto))
    }

    @GetMapping("/todos/{todoId}/managers")
    fun getMembers(
        @PathVariable todoId: Long
    ): ResponseEntity<List<ManagerFindAllRespDto>> {
        return ResponseEntity.ok(managerService.getManagers(todoId))
    }

    @DeleteMapping("/todos/{todoId}/managers/{managerId}")
    fun deleteManager(
        @AuthenticationPrincipal authUser: AuthUser,
        @PathVariable todoId: Long,
        @PathVariable managerId: Long
    ) {
        managerService.deleteManager(authUser, todoId, managerId)
    }
}