package com.goormthon.spot

interface SpotTagRepository {
    fun save(create: SpotTag.Create): SpotTag.Info
}
