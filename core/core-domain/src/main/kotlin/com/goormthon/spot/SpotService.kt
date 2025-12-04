package com.goormthon.spot

import com.goormthon.support.tx.Tx
import org.springframework.stereotype.Service

@Service
class SpotService(
    private val spotRepository: SpotRepository,
    private val spotTagRepository: SpotTagRepository,
) {
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
}
