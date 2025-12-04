package com.goormthon.storage.rdb.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import com.goormthon.enums.AuthorityType
import com.goormthon.storage.rdb.support.BaseEntity
import com.goormthon.user.User
import com.goormthon.user.UserCredential
import com.goormthon.user.UserProfile
import com.goormthon.user.criteria.UserCriteria

@Entity
@Table(
    name = "t_user",
    indexes = [
        Index(name = "idx_user_user_key", columnList = "user_key"),
    ],
)
class UserEntity(
    @Column(name = "user_key")
    val userKey: String,
    var name: String,
    var keyword: String,
    var email: String?,
    var password: String?,
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10)")
    var role: AuthorityType = AuthorityType.USER,
) : BaseEntity() {
    constructor(
        criteria: UserCriteria.Create,
    ) : this(
        userKey = criteria.userKey,
        name = criteria.name,
        keyword = criteria.keyword,
        email = criteria.email,
        password = criteria.password,
        role = criteria.role,
    )

    fun toUser() =
        User(
            id = id!!,
            key = userKey,
            role = role,
        )

    fun toProfile() =
        UserProfile(
            id = id!!,
            key = userKey,
            name = name,
            email = email,
            keyword = keyword,
            role = role,
            createdAt = createdAt,
        )

    fun update(criteria: UserCriteria.Update) {
        criteria.name?.let { this.name = it }
        criteria.email?.let { this.email = it }
        criteria.password?.let { this.password = it }
    }

    fun toUserCredential() =
        UserCredential(
            userId = id!!,
            userKey = userKey,
            email = email,
            password = password,
        )
}
