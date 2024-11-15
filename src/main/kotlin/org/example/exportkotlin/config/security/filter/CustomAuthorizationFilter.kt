package org.example.exportkotlin.config.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.exportkotlin.config.security.AuthUser
import org.example.exportkotlin.config.security.JwtProvider
import org.example.exportkotlin.domain.user.User
import org.example.exportkotlin.domain.user.UserRole
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

class CustomAuthorizationFilter(
    authenticationManager: AuthenticationManager,
    private val jwtProvider: JwtProvider,
) : BasicAuthenticationFilter(authenticationManager) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestURI = request.requestURI

        if (requestURI.startsWith("/auth")) {
            filterChain.doFilter(request, response)
            return
        }


        val tokenValue = (request.getHeader("Authorization")
            ?.let { jwtProvider.subStringToken(it) }
            ?: throw CustomApiException(ErrorCode.TOKEN_NOT_FOUND))

        log.info("헤더에 있는 토큰 정보 : $tokenValue")
        jwtProvider.validateToken(tokenValue)
        val extractClaims = jwtProvider.extractClaims(tokenValue)

        val userId = extractClaims.subject.toLong()
        val email = extractClaims["email"].toString()
        val nickname = extractClaims["nickname"].toString()
        val userRole = UserRole.valueOf(extractClaims["userRole"] as String)
        val authUser = AuthUser(User(userId, email, userRole, nickname))
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(authUser, null, authUser.authorities)

        filterChain.doFilter(request, response)
    }
}