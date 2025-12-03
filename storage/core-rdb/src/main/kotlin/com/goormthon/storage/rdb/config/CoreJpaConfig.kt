package com.goormthon.storage.rdb.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EntityScan(
    basePackages = [
        "com.goormthon.storage.rdb",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.goormthon.storage.rdb",
    ],
)
class CoreJpaConfig
