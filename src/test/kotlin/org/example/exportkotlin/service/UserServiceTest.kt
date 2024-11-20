package org.example.exportkotlin.service

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.exportkotlin.domain.user.UserRepository
import org.example.exportkotlin.dto.user.req.UserChangePasswordReqDto
import org.example.exportkotlin.dto.user.resp.UserFindOneRespDto
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.makeUserMock
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceTest : BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val userService = UserService(userRepository, passwordEncoder)

    given("사용자가 존재할 때") {
        val userId = 1L
        val user = makeUserMock(id = 1L, email = "test@exmapl.com")

        `when`("사용자 정보를 조회하면") {
            every { userRepository.findByIdOrNull(userId) } returns user

            then("정상 결과를 반환한다.") {
                val result = userService.getUser(userId)
                result shouldBeEqualToComparingFields UserFindOneRespDto.of(user)
                verify(exactly = 1) { userRepository.findByIdOrNull(userId) }
            }
        }

        `when`("존재하지 않는 사용자를 요청하면") {
            every { userRepository.findByIdOrNull(userId) } returns null

            then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    userService.getUser(userId)
                }.message shouldBe "해당 유저를 찾을 수 없습니다."
                verify(exactly = 1) { userRepository.findByIdOrNull(userId) }
            }
        }
    }

    given("사용자 비밀번호 변경을 요청할 때") {
        val userId = 1L
        var user = makeUserMock(id = 1L)
        val dto = UserChangePasswordReqDto("oldPassword", "newPassword")

        `when`("사용자를 찾을 수 없는 경우") {
            every { userRepository.findByIdOrNull(userId) } returns null

            then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    userService.changePassword(userId, dto)
                }.message shouldBe "해당 유저를 찾을 수 없습니다."
                verify(exactly = 1) { userRepository.findByIdOrNull(userId) }
            }
        }

        `when`("새 비밀번호가 기존 비밀번호와 일치하는 경우") {
            every { userRepository.findByIdOrNull(userId) } returns user
            every { passwordEncoder.matches(dto.newPassword, any()) } returns true

            then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    userService.changePassword(userId, dto)
                }.message shouldBe "새 비밀번호는 기존 비밀번호와 같을 수 없습니다."
                verify(exactly = 1) { userRepository.findByIdOrNull(userId) }
                verify(exactly = 1) { passwordEncoder.matches(dto.newPassword, any()) }
            }
        }

        `when`("기존 비밀번호가 일치하지 않는 경우") {
            every { userRepository.findByIdOrNull(userId) } returns user
            every { passwordEncoder.matches(dto.newPassword, any()) } returns false
            every { passwordEncoder.matches(dto.oldPassword, any()) } returns false

            then("CustomApiException 이 발생한다.") {
                shouldThrowExactly<CustomApiException> {
                    userService.changePassword(userId, dto)
                }.message shouldBe "잘못된 비밀번호입니다."
                verify(exactly = 1) { userRepository.findByIdOrNull(userId) }
                verify(exactly = 2) { passwordEncoder.matches(any(), any()) }
            }
        }

        `when`("새 비밀번호가 기존 비밀번호와 다르고 기존 비밀번호와 패스워드가 일치하는 경우") {
            every { userRepository.findByIdOrNull(userId) } returns user
            every { passwordEncoder.matches(dto.newPassword, any()) } returns false
            every { passwordEncoder.matches(dto.oldPassword, any()) } returns true
            every { passwordEncoder.encode(dto.newPassword) } returns "encodedNewPassword"

            then("비밀번호가 변경된다.") {
                userService.changePassword(userId, dto)
                verify(exactly = 1) { userRepository.findByIdOrNull(userId) }
                verify(exactly = 2) { passwordEncoder.matches(any(), any()) }
                verify(exactly = 1) { passwordEncoder.encode(any()) }
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerTest
}