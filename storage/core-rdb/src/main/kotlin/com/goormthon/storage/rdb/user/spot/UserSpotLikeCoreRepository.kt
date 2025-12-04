package com.goormthon.storage.rdb.user.spot

import com.goormthon.user.spot.UserSpotLikeRepository
import org.springframework.stereotype.Repository

@Repository
class UserSpotLikeCoreRepository(
    private val userSpotLikeJpaRepository: UserSpotLikeJpaRepository,
) : UserSpotLikeRepository
