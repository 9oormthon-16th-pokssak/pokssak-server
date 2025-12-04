package com.goormthon.storage.rdb.user.spot

import org.springframework.data.jpa.repository.JpaRepository

interface UserSpotVisitJpaRepository : JpaRepository<UserSpotVisitEntity, Long> {
    fun findByUserIdAndSpotIdAndDeletedAtIsNull(userId: Long, spotId: Long): UserSpotVisitEntity?
}
