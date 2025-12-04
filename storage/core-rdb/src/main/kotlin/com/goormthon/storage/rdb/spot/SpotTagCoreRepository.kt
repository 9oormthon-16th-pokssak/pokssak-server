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

    override fun saveAll(creates: List<SpotTag.Create>): List<SpotTag.Info> {
        val entities = creates.map { SpotTagEntity(it) }
        val savedEntities = spotTagJpaRepository.saveAll(entities)
        return savedEntities.map { it.toSpotTag() }
    }
}
