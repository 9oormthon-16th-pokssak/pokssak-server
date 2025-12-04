package com.goormthon.storage.rdb.spot

import org.springframework.data.jpa.repository.JpaRepository

interface SpotJpaRepository : JpaRepository<SpotEntity, Long>
