package org.example.exportkotlin.config.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.config.security.JwtProvider
import org.example.exportkotlin.config.security.dto.LoginReqDto
import org.example.exportkotlin.config.security.dto.LoginRespDto
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException

class CustomAuthenticationFilter(
    authenticationManager: AuthenticationManager,
    private val jwtProvider: JwtProvider,

) : UsernamePasswordAuthenticationFilter(authenticationManager) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val mapper = jacksonObjectMapper()

    init {
        setFilterProcessesUrl("/auth/login")
    }

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        try {
            val loginUser = mapper.readValue(request?.reader, LoginReqDto::class.java)
            return this.authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginUser.email, loginUser.password))
        } catch (e: IOException) {
            throw CustomApiException(ErrorCode.UNKNOWN_AUTHENTICATED)
        }
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        val mapper = ObjectMapper()
        SecurityContextHolder.getContext().authentication = authResult
        val authUser = authResult?.principal as AuthUser
        val token = jwtProvider.createToken(authUser)

        requireNotNull(response).also {
            it.addHeader("Authorization", token)
            it.contentType = MediaType.APPLICATION_JSON_VALUE
            it.characterEncoding = "UTF-8"
            it.status = HttpServletResponse.SC_OK
            log.info("토큰 정상 발급 $token")
            it.writer.write(mapper.writeValueAsString(LoginRespDto(authUser.getId(), token)))
        }
    }
}