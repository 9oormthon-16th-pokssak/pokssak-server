package com.goormthon.storage.rdb.spot

import com.goormthon.spot.SpotTag
import com.goormthon.spot.SpotTagRepository
import org.springframework.stereotype.Repository

@Repository
class SpotTagCoreRepository(
    private val spotTagJpaRepository: SpotTagJpaRepository,
) : SpotTagRepository {
    override fun save(create: SpotTag.Create): SpotTag.Info {
        val savedEntity = spotTagJpaRepository.save(SpotTagEntity(create))
        return savedEntity.toSpotTag()
    }
}
