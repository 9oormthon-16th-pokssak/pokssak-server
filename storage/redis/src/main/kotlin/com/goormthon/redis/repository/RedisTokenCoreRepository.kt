package com.goormthon.redis.repository

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import com.goormthon.auth.Provider
import com.goormthon.auth.RedisTokenRepository
import com.goormthon.auth.TokenWithAuthenticationResult
import com.goormthon.enums.ErrorType
import com.goormthon.error.ErrorException

@Repository
class RedisTokenCoreRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : RedisTokenRepository {
    override fun create(
        accessToken: String,
        refreshToken: String,
        provider: Provider,
        accessTokenExpiration: Long,
        refreshTokenExpiration: Long,
    ): TokenWithAuthenticationResult {
        val tokenWithAuthenticationResult =
            TokenWithAuthenticationResult(
                accessToken = accessToken,
                refreshToken = refreshToken,
                provider = provider,
            )

        redisTemplate.opsForValue().apply {
            set(
                accessToken,
                objectMapper.writeValueAsString(tokenWithAuthenticationResult),
                Duration.ofSeconds(accessTokenExpiration * 60L),
            )
            set(
                refreshToken,
                objectMapper.writeValueAsString(tokenWithAuthenticationResult),
                Duration.ofSeconds(refreshTokenExpiration * 60L),
            )
        }
        return tokenWithAuthenticationResult
    }

    override fun findByToken(token: String): TokenWithAuthenticationResult {
        redisTemplate.opsForValue().get(token)?.let {
            return objectMapper.readValue(it, TokenWithAuthenticationResult::class.java)
        } ?: throw ErrorException(ErrorType.INVALID_TOKEN)
    }

    override fun findBy(accessToken: String): Provider? =
        redisTemplate.opsForValue().get(accessToken)?.let {
            val result = objectMapper.readValue(it, TokenWithAuthenticationResult::class.java)
            Provider(
                userId = result.provider.userId,
                userKey = result.provider.userKey,
                grantedAuthorities = result.provider.grantedAuthorities,
            )
        }

    override fun deleteToken(token: String) {
        redisTemplate.delete(token)
    }

    override fun deleteAllToken(token: String) {
        val tokenWithAuthenticationResult =
            redisTemplate.opsForValue().get(token)?.let {
                objectMapper.readValue(it, TokenWithAuthenticationResult::class.java)
            }

        tokenWithAuthenticationResult?.accessToken?.let { redisTemplate.delete(it) }
        tokenWithAuthenticationResult?.refreshToken?.let { redisTemplate.delete(it) }
    }
}
