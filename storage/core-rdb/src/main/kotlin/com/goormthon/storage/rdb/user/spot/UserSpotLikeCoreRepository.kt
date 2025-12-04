package com.goormthon.storage.rdb.user.spot

import com.goormthon.support.tx.Tx
import com.goormthon.user.spot.UserSpotLikeRepository
import org.springframework.stereotype.Repository

@Repository
class UserSpotLikeCoreRepository(
    private val userSpotLikeJpaRepository: UserSpotLikeJpaRepository,
) : UserSpotLikeRepository {
    override fun existsByUserIdAndSpotId(userId: Long, spotId: Long): Boolean = Tx.readable {
        userSpotLikeJpaRepository.existsByUserIdAndSpotIdAndDeletedAtIsNull(userId, spotId)
    }
}
