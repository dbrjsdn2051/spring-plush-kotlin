package org.example.exportkotlin.config.security

import jakarta.servlet.DispatcherType
import org.example.exportkotlin.config.security.filter.CustomAuthenticationFilter
import org.example.exportkotlin.config.security.filter.CustomAuthorizationFilter
import org.example.exportkotlin.config.security.filter.GlobalExceptionHandlerFilter
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val jwtProvider: JwtProvider
) {

    @Bean
    @Throws(Exception::class)
    fun securityFilerChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                    .requestMatchers("/error").permitAll()
//                    .requestMatchers(PathRequest.toH2Console()).permitAll()
                    .requestMatchers("/auth/signup", "/auth/register").permitAll()
                    .anyRequest().authenticated()
            }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .anonymous { it.disable() }
            .headers { it.frameOptions(HeadersConfigurer<HttpSecurity>.FrameOptionsConfig::sameOrigin) }
            .addFilterBefore(GlobalExceptionHandlerFilter(), CustomAuthorizationFilter::class.java)
            .addFilterBefore(
                CustomAuthorizationFilter(authenticationManager(authenticationConfiguration), jwtProvider),
                CustomAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CustomAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    @ConditionalOnProperty(name = ["spring.h2.console.enabled"], havingValue = "true")
    fun configureH2ConsoleEnable(): WebSecurityCustomizer {
        return WebSecurityCustomizer { it.ignoring().requestMatchers(PathRequest.toH2Console()) }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder();
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}