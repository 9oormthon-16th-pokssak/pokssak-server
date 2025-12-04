package com.goormthon.spot

import com.goormthon.support.tx.Tx
import org.springframework.stereotype.Service

@Service
class SpotService(
    private val spotRepository: SpotRepository,
    private val spotTagRepository: SpotTagRepository,
) {
    fun findAllSpots(): List<Spot.Info> =
        spotRepository.findAll()

    fun createSpotWithTags(create: Spot.Create, tagNames: List<String>): Spot.Info = Tx.requiresNew {
        val spotInfo = spotRepository.save(create)
        val spotTagsCreate = tagNames.map { tagName ->
            SpotTag.Create(
                spotId = spotInfo.id,
                name = tagName,
            )
        }
        spotTagRepository.saveAll(spotTagsCreate)
        return@requiresNew spotInfo
    }

    fun findBy(spotId: Long): FindSpotResult {
        val spot = spotRepository.findBy(spotId)
        val spotTags = spotTagRepository.findBySpotId(spotId).map { it.name }
        return FindSpotResult(
            spot = spot,
            tags = spotTags,
        )
    }

    fun findAllBy(
        keyword: String?,
        swLat: Double?,
        swLng: Double?,
        neLat: Double?,
        neLng: Double?,
        userId: Long,
    ): List<Spot.Info> = spotRepository.findAllBy(keyword, swLat, swLng, neLat, neLng)

    fun updateSpotWeight(spotId: Long, weight: Double) {
        spotRepository.updateWeight(spotId, weight)
    }
}
