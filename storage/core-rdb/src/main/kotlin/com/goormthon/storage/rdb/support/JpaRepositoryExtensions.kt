package com.goormthon.storage.rdb.support

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import com.goormthon.enums.ErrorType
import com.goormthon.error.ErrorException

fun <T : BaseEntity> JpaRepository<T, Long>.findByIdOrElseThrow(id: Long): T {
    val value = findByIdOrNull(id) ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
    return value
}

fun <T : BaseEntity> JpaRepository<T, Long>.findByIdAndDeletedAtIsNullOrElseThrow(id: Long): T {
    val value = findByIdOrNull(id).takeIf { it?.deletedAt == null } ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
    return value
}
