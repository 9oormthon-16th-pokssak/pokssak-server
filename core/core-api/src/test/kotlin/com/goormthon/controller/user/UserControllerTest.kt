package com.goormthon.controller.user

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.goormthon.auth.Token
import com.goormthon.controller.auth.request.AuthRequest
import com.goormthon.controller.user.request.UserRequest
import com.goormthon.enums.AuthorityType
import com.goormthon.storage.rdb.user.UserEntity
import com.goormthon.storage.rdb.user.UserJpaRepository
import com.goormthon.support.BaseIntegrationTest

@DisplayName("UserController 통합 테스트")
class UserControllerTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    private fun createUserAndGetToken(
        name: String = "홍길동",
        email: String = "test@example.com",
        password: String = "password123!",
        role: AuthorityType = AuthorityType.USER,
    ): Pair<UserEntity, Token> {
        val signupRequest =
            AuthRequest.Signup(
                name = name,
                email = email,
                password = password,
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

        val user = userJpaRepository.findByEmail(email)!!

        // ADMIN 권한이 필요한 경우 role 변경 후 재로그인
        if (role == AuthorityType.ADMIN) {
            user.role = AuthorityType.ADMIN
            userJpaRepository.save(user)

            // 새로운 토큰 발급을 위해 재로그인
            val signinRequest =
                AuthRequest.Signin(
                    email = email,
                    password = password,
                )

            val signinResult =
                mockMvc.perform(
                    post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signinRequest)),
                )
                    .andExpect(status().isOk)
                    .andReturn()

            val signinResponse = objectMapper.readTree(signinResult.response.contentAsString)
            val token =
                Token(
                    accessToken = signinResponse.get("data").get("accessToken").asText(),
                    refreshToken = signinResponse.get("data").get("refreshToken").asText(),
                )
            return Pair(user, token)
        } else {
            val token =
                Token(
                    accessToken = response.get("data").get("accessToken").asText(),
                    refreshToken = response.get("data").get("refreshToken").asText(),
                )
            return Pair(user, token)
        }
    }

    @Test
    @DisplayName("사용자 목록 조회 성공 - ADMIN 권한으로 조회")
    fun getUsersSuccessWithAdmin() {
        // given
        createUserAndGetToken(
            email = "user1@example.com",
            role = AuthorityType.USER,
        )
        createUserAndGetToken(
            email = "user2@example.com",
            role = AuthorityType.USER,
        )
        val (_, adminToken) =
            createUserAndGetToken(
                email = "admin@example.com",
                role = AuthorityType.ADMIN,
            )

        // when & then
        mockMvc.perform(
            get("/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${adminToken.accessToken}")
                .param("page", "0")
                .param("size", "20"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
            .andExpect(jsonPath("$.data.content.length()").value(3))
    }

    @Test
    @DisplayName("사용자 목록 조회 실패 - 인증 토큰 없음 (UNAUTHORIZED)")
    fun getUsersFailWithoutToken() {
        // when & then
        mockMvc.perform(get("/users"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("사용자 조회 성공 - ADMIN 권한으로 다른 사용자 조회")
    fun getUserSuccessWithAdmin() {
        // given
        val (user, _) =
            createUserAndGetToken(
                email = "user@example.com",
                role = AuthorityType.USER,
            )
        val (_, adminToken) =
            createUserAndGetToken(
                email = "admin@example.com",
                role = AuthorityType.ADMIN,
            )

        // when & then
        mockMvc.perform(
            get("/users/${user.id}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${adminToken.accessToken}"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.id").value(user.id))
            .andExpect(jsonPath("$.data.email").value("user@example.com"))
    }

    @Test
    @DisplayName("사용자 조회 성공 - 본인 정보 조회")
    fun getUserSuccessWithOwnData() {
        // given
        val (user, token) =
            createUserAndGetToken(
                email = "user@example.com",
                role = AuthorityType.USER,
            )

        // when & then
        mockMvc.perform(
            get("/users/${user.id}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${token.accessToken}"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.id").value(user.id))
            .andExpect(jsonPath("$.data.email").value("user@example.com"))
    }

    @Test
    @DisplayName("사용자 수정 성공 - 본인 정보 수정")
    fun modifyUserSuccessWithOwnData() {
        // given
        val (user, token) =
            createUserAndGetToken(
                email = "user@example.com",
                role = AuthorityType.USER,
            )

        val updateRequest =
            UserRequest.Update(
                name = "김철수",
                email = "updated@example.com",
                password = null,
            )

        // when & then
        mockMvc.perform(
            put("/users/${user.id}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${token.accessToken}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.name").value("김철수"))
            .andExpect(jsonPath("$.data.email").value("updated@example.com"))
    }

    @Test
    @DisplayName("사용자 수정 성공 - ADMIN 권한으로 다른 사용자 수정")
    fun modifyUserSuccessWithAdmin() {
        // given
        val (user, _) =
            createUserAndGetToken(
                email = "user@example.com",
                role = AuthorityType.USER,
            )
        val (_, adminToken) =
            createUserAndGetToken(
                email = "admin@example.com",
                role = AuthorityType.ADMIN,
            )

        val updateRequest =
            UserRequest.Update(
                name = "관리자 수정",
                email = null,
                password = null,
            )

        // when & then
        mockMvc.perform(
            put("/users/${user.id}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${adminToken.accessToken}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.name").value("관리자 수정"))
    }

    @Test
    @DisplayName("사용자 삭제 성공 - 본인 계정 삭제")
    fun deleteUserSuccessWithOwnData() {
        // given
        val (user, token) =
            createUserAndGetToken(
                email = "user@example.com",
                role = AuthorityType.USER,
            )

        // when & then
        mockMvc.perform(
            delete("/users/${user.id}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${token.accessToken}"),
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("사용자 삭제 성공 - ADMIN 권한으로 다른 사용자 삭제")
    fun deleteUserSuccessWithAdmin() {
        // given
        val (user, _) =
            createUserAndGetToken(
                email = "user@example.com",
                role = AuthorityType.USER,
            )
        val (_, adminToken) =
            createUserAndGetToken(
                email = "admin@example.com",
                role = AuthorityType.ADMIN,
            )

        // when & then
        mockMvc.perform(
            delete("/users/${user.id}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${adminToken.accessToken}"),
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("사용자 삭제 실패 - 인증 토큰 없음 (UNAUTHORIZED)")
    fun deleteUserFailWithoutToken() {
        // given
        val (user, _) =
            createUserAndGetToken(
                email = "user@example.com",
                role = AuthorityType.USER,
            )

        // when & then
        mockMvc.perform(delete("/users/${user.id}"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("사용자 조회 실패 - 잘못된 토큰 (UNAUTHORIZED)")
    fun getUserFailWithInvalidToken() {
        // when & then
        mockMvc.perform(
            get("/users/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid_token"),
        )
            .andExpect(status().isUnauthorized)
    }
}
