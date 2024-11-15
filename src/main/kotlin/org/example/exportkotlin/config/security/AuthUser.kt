package org.example.exportkotlin.config.security

import org.example.exportkotlin.domain.user.User
import org.example.exportkotlin.domain.user.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class AuthUser(
    private val user: User
) : UserDetails {


    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_" + user.userRole.toString()))
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    fun getRole():UserRole{
        return user.userRole
    }

    fun getNickname():String{
        return user.nickname
    }

    fun getId() : Long{
        return user.id!!
    }
}