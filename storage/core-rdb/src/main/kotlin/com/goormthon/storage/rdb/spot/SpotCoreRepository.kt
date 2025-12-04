package com.goormthon.storage.rdb.spot

import com.goormthon.spot.Spot
import com.goormthon.spot.SpotRepository
import com.goormthon.storage.rdb.support.findByIdAndDeletedAtIsNullOrElseThrow
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

    override fun findBy(spotId: Long): Spot.Info = Tx.readable {
        spotJpaRepository.findByIdAndDeletedAtIsNullOrElseThrow(spotId).toInfo()
    }

    override fun findAllBy(keyword: String?, swLat: Double?, swLng: Double?, neLat: Double?, neLng: Double?): List<Spot.Info> =
        Tx.readable {
        spotJpaRepository.findAllBy(keyword, swLat, swLng, neLat, neLng).map { it.toInfo() }
    }

    override fun updateWeight(spotId: Long, weight: Double) = Tx.requiresNew {
        spotJpaRepository.updateWeight(spotId, weight)
    }
}
