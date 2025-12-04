package com.goormthon.spot

interface SpotTagRepository {
    fun save(create: SpotTag.Create): SpotTag.Info
    fun saveAll(creates: List<SpotTag.Create>): List<SpotTag.Info>
    fun findBySpotId(spotId: Long): List<SpotTag.Info>
}
