package com.goormthon.storage.rdb.user.spot

import com.goormthon.user.spot.UserSpotVisitRepository
import org.springframework.stereotype.Repository

@Repository
class UserSpotVisitCoreRepository(
    private val userSpotVisitJpaRepository: UserSpotVisitJpaRepository,
) : UserSpotVisitRepository
