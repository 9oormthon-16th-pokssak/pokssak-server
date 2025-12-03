package com.goormthon.storage.rdb.auth
import org.springframework.data.jpa.repository.JpaRepository
import com.goormthon.enums.AuthenticationEntityStatus

interface AuthenticationHistoryJpaRepository : JpaRepository<AuthenticationHistoryEntity, Long> {
    fun findAllByUserKeyAndEntityStatus(
        userKey: String,
        status: AuthenticationEntityStatus,
    ): List<AuthenticationHistoryEntity>

    fun findByAccessToken(accessToken: String): AuthenticationHistoryEntity?

    fun findAllByUserKey(userKey: String): List<AuthenticationHistoryEntity>
}
