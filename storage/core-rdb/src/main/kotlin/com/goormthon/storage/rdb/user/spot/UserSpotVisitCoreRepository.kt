package com.goormthon.storage.rdb.user.spot

import com.goormthon.support.tx.Tx
import com.goormthon.user.spot.UserSpotVisitRepository
import org.springframework.stereotype.Repository

@Repository
class UserSpotVisitCoreRepository(
    private val userSpotVisitJpaRepository: UserSpotVisitJpaRepository,
) : UserSpotVisitRepository {
    override fun existsByUserIdAndSpotId(userId: Long, spotId: Long): Boolean =
        Tx.readable {
            userSpotVisitJpaRepository.existsByUserIdAndSpotIdAndDeletedAtIsNull(userId, spotId)
        }

    override fun visitSpot(userId: Long, spotId: Long) =
        Tx.writeable {
            val existingVisit = userSpotVisitJpaRepository.findByUserIdAndSpotIdAndDeletedAtIsNull(userId, spotId)
            if (existingVisit == null) {
                val newVisit = UserSpotVisitEntity(userId = userId, spotId = spotId)
                userSpotVisitJpaRepository.save(newVisit)
            }
        }
}
