package com.goormthon.spot

interface SpotRepository {
    fun save(create: Spot.Create): Spot.Info
    fun findAll(): List<Spot.Info>
}
