package com.goormthon.spot

interface SpotRepository {
    fun save(create: Spot.Create): Spot.Info
    fun findAll(): List<Spot.Info>
    fun findBy(spotId: Long): Spot.Info
    fun findAllBy(keyword: String?, swLat: Double?, swLng: Double?, neLat: Double?, neLng: Double?): List<Spot.Info>
    fun updateWeight(spotId: Long, weight: Double)
    fun findAllByIds(spotIds: List<Long>): List<Spot.Info>
}
