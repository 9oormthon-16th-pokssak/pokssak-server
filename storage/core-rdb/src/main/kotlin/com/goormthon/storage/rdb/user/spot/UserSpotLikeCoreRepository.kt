package com.goormthon.storage.rdb.user.spot

import com.goormthon.support.tx.Tx
import com.goormthon.user.spot.UserSpotLike
import com.goormthon.user.spot.UserSpotLikeRepository
import org.springframework.stereotype.Repository

@Repository
class UserSpotLikeCoreRepository(
    private val userSpotLikeJpaRepository: UserSpotLikeJpaRepository,
) : UserSpotLikeRepository {
    override fun existsByUserIdAndSpotId(userId: Long, spotId: Long): Boolean = Tx.readable {
        userSpotLikeJpaRepository.existsByUserIdAndSpotIdAndDeletedAtIsNull(userId, spotId)
    }

    override fun likeSpot(userId: Long, spotId: Long) = Tx.writeable {
        val existingLike = userSpotLikeJpaRepository.findByUserIdAndSpotIdAndDeletedAtIsNull(userId, spotId)
        if (existingLike == null) {
            val newLike = UserSpotLikeEntity(userId = userId, spotId = spotId)
            userSpotLikeJpaRepository.save(newLike)
        }
    }

    override fun dislikeSpot(userId: Long, spotId: Long): Unit =
        Tx.writeable {
            val existingLike = userSpotLikeJpaRepository.findByUserIdAndSpotIdAndDeletedAtIsNull(userId, spotId)
            existingLike?.let {
                userSpotLikeJpaRepository.delete(it)
            }
        }

    override fun findAllByUserId(userId: Long): List<UserSpotLike.Info> = Tx.readable {
        val entities = userSpotLikeJpaRepository.findAllByUserIdAndDeletedAtIsNull(userId)
        return@readable entities.map { it.toInfo() }
    }
}
