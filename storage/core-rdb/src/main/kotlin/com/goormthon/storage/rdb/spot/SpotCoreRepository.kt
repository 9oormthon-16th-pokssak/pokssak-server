package com.goormthon.storage.rdb.spot

import com.goormthon.spot.Spot
import com.goormthon.spot.SpotRepository
import com.goormthon.support.tx.Tx
import org.springframework.stereotype.Repository

@Repository
class SpotCoreRepository(
    private val spotJpaRepository: SpotJpaRepository,
) : SpotRepository {
    override fun save(create: Spot.Create): Spot.Info {
        val savedEntity = spotJpaRepository.save(SpotEntity(create))
        return savedEntity.toInfo()
    }

    override fun findAll(): List<Spot.Info> = Tx.readable {
        return@readable spotJpaRepository.findAll().map { it.toInfo() }
    }
}
