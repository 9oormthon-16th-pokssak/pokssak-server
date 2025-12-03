package com.goormthon.auth

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.goormthon.controller.auth.request.AuthRequest
import com.goormthon.enums.ErrorType
import com.goormthon.error.ErrorException
import com.goormthon.support.BaseIntegrationTest

@DisplayName("Redis Token Repository 통합 테스트")
class RedisTokenRepositoryTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var redisTokenRepository: RedisTokenRepository

    @Test
    @DisplayName("토큰 저장 성공 - Redis에 토큰과 인증 정보가 저장된다")
    fun saveTokenSuccessfully() {
        // given
        val signupRequest =
            AuthRequest.Signup(
                name = "홍길동",
                email = "test@example.com",
                password = "password123!",
            )

        val result =
            mockMvc.perform(
                post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupRequest)),
            )
                .andExpect(status().isCreated)
                .andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        val accessToken = response.get("data").get("accessToken").asText()
        val refreshToken = response.get("data").get("refreshToken").asText()

        // when - Redis에서 토큰으로 조회
        val accessTokenResult = redisTokenRepository.findByToken(accessToken)
        val refreshTokenResult = redisTokenRepository.findByToken(refreshToken)

        // then - 토큰 정보가 Redis에 저장되어 있음
        assertThat(accessTokenResult).isNotNull
        assertThat(accessTokenResult.accessToken).isEqualTo(accessToken)
        assertThat(accessTokenResult.refreshToken).isEqualTo(refreshToken)
        assertThat(accessTokenResult.provider.userKey).isNotBlank()

        assertThat(refreshTokenResult).isNotNull
        assertThat(refreshTokenResult.accessToken).isEqualTo(accessToken)
        assertThat(refreshTokenResult.refreshToken).isEqualTo(refreshToken)
    }

    @Test
    @DisplayName("토큰 조회 성공 - accessToken으로 Provider 조회")
    fun findProviderByAccessToken() {
        // given
        val signupRequest =
            AuthRequest.Signup(
                name = "홍길동",
                email = "test@example.com",
                password = "password123!",
            )

        val result =
            mockMvc.perform(
                post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupRequest)),
            )
                .andExpect(status().isCreated)
                .andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        val accessToken = response.get("data").get("accessToken").asText()

        // when
        val provider = redisTokenRepository.findBy(accessToken)

        // then
        assertThat(provider).isNotNull
        assertThat(provider!!.userKey).isNotBlank()
        assertThat(provider.grantedAuthorities).contains("USER")
    }

    @Test
    @DisplayName("토큰 조회 실패 - 존재하지 않는 토큰 조회")
    fun findByInvalidToken() {
        // given
        val invalidToken = "invalid_token_12345"

        // when & then
        val exception =
            assertThrows<ErrorException> {
                redisTokenRepository.findByToken(invalidToken)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.INVALID_TOKEN)
    }

    @Test
    @DisplayName("토큰 삭제 성공 - accessToken 삭제")
    fun deleteTokenSuccessfully() {
        // given
        val signupRequest =
            AuthRequest.Signup(
                name = "홍길동",
                email = "test@example.com",
                password = "password123!",
            )

        val result =
            mockMvc.perform(
                post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupRequest)),
            )
                .andExpect(status().isCreated)
                .andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        val accessToken = response.get("data").get("accessToken").asText()

        // when
        redisTokenRepository.deleteToken(accessToken)

        // then - 삭제 후 조회 시 예외 발생
        assertThrows<ErrorException> {
            redisTokenRepository.findByToken(accessToken)
        }
    }

    @Test
    @DisplayName("모든 토큰 삭제 성공 - accessToken과 refreshToken 모두 삭제")
    fun deleteAllTokensSuccessfully() {
        // given
        val signupRequest =
            AuthRequest.Signup(
                name = "홍길동",
                email = "test@example.com",
                password = "password123!",
            )

        val result =
            mockMvc.perform(
                post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupRequest)),
            )
                .andExpect(status().isCreated)
                .andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        val accessToken = response.get("data").get("accessToken").asText()
        val refreshToken = response.get("data").get("refreshToken").asText()

        // when
        redisTokenRepository.deleteAllToken(accessToken)

        // then - 삭제 후 두 토큰 모두 조회 실패
        assertThrows<ErrorException> {
            redisTokenRepository.findByToken(accessToken)
        }
        assertThrows<ErrorException> {
            redisTokenRepository.findByToken(refreshToken)
        }
    }

    @Test
    @DisplayName("로그인 후 토큰 저장 확인 - 새로운 토큰이 Redis에 저장된다")
    fun saveTokenAfterSignin() {
        // given - 회원가입
        val signupRequest =
            AuthRequest.Signup(
                name = "홍길동",
                email = "test@example.com",
                password = "password123!",
            )

        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)),
        )
            .andExpect(status().isCreated)

        // 로그인
        val signinRequest =
            AuthRequest.Signin(
                email = "test@example.com",
                password = "password123!",
            )

        val result =
            mockMvc.perform(
                post("/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signinRequest)),
            )
                .andExpect(status().isOk)
                .andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        val accessToken = response.get("data").get("accessToken").asText()
        val refreshToken = response.get("data").get("refreshToken").asText()

        // when - Redis에서 토큰으로 조회
        val tokenResult = redisTokenRepository.findByToken(accessToken)

        // then
        assertThat(tokenResult).isNotNull
        assertThat(tokenResult.accessToken).isEqualTo(accessToken)
        assertThat(tokenResult.refreshToken).isEqualTo(refreshToken)
        assertThat(tokenResult.provider.userKey).isNotBlank()
    }

    @Test
    @DisplayName("토큰 조회 실패 - 빈 토큰")
    fun findByEmptyToken() {
        // when & then
        val provider = redisTokenRepository.findBy("")

        assertThat(provider).isNull()
    }
}
