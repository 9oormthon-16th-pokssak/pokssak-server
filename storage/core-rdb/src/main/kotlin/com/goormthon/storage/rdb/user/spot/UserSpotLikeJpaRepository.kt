package com.goormthon.storage.rdb.user.spot

import org.springframework.data.jpa.repository.JpaRepository

interface UserSpotLikeJpaRepository : JpaRepository<UserSpotLikeEntity, Long> {
    fun findByUserIdAndSpotIdAndDeletedAtIsNull(userId: Long, spotId: Long): UserSpotLikeEntity?
    fun existsByUserIdAndSpotIdAndDeletedAtIsNull(userId: Long, spotId: Long): Boolean
}
