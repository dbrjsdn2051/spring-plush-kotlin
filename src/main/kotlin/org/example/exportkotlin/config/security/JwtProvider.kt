package org.example.exportkotlin.config.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtProvider {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val TOKEN_TIME = 24L * 60 * 60 * 1000L
    }

    private final val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jwt.secret.key}")
    lateinit var secretKey: String
    private lateinit var key: Key
    private val signatureAlgorithm: SignatureAlgorithm = SignatureAlgorithm.HS256

    @PostConstruct
    fun init() {
        val bytes = Base64.getDecoder().decode(secretKey)
        key = Keys.hmacShaKeyFor(bytes)
    }

    fun createToken(authUser: AuthUser): String {
        return BEARER_PREFIX + Jwts.builder()
            .setSubject(authUser.getId().toString())
            .claim("email", authUser.username)
            .claim("userRole", authUser.getRole())
            .claim("nickname", authUser.getNickname())
            .setExpiration(Date(Date().time + TOKEN_TIME))
            .setIssuedAt(Date())
            .signWith(key, signatureAlgorithm)
            .compact()
    }

    fun subStringToken(tokenValue: String): String {
        return tokenValue.replace(BEARER_PREFIX, "")
    }

    fun extractClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun validateToken(token: String?) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        } catch (e: SecurityException) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명입니다. ")
        } catch (e: MalformedJwtException) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명입니다. ")
        } catch (e: ExpiredJwtException) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.")
        } catch (e: UnsupportedJwtException) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰입니다. ")
        }
    }

}