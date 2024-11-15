package org.example.exportkotlin.domain.user

import jakarta.persistence.*
import org.example.exportkotlin.common.entity.BaseEntity

@Entity
@Table(name = "users")
class User(

    val email: String,
    var password: String,
    @Enumerated(EnumType.STRING)
    val userRole: UserRole,
    val nickname: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity() {

    constructor(
        userId: Long,
        email: String,
        userRole: UserRole,
        nickname: String,
    ) : this(email, "", userRole, nickname, userId)

    fun changePassword(password: String) {
        this.password = password;
    }

}