package com.goormthon.user

import com.goormthon.support.page.OffsetPageRequest
import com.goormthon.support.page.Page
import com.goormthon.user.criteria.UserCriteria

interface UserRepository {
    fun save(criteria: UserCriteria.Create): User
    fun findById(userId: Long): User
    fun findByKey(userKey: String): User
    fun existsByEmail(email: String): Boolean
    fun findCredentialByEmail(email: String): UserCredential?
    fun findProfileById(userId: Long): UserProfile
    fun findAllBy(request: OffsetPageRequest): Page<UserProfile>
    fun update(userId: Long, criteria: UserCriteria.Update): UserProfile
    fun delete(userId: Long)
}
