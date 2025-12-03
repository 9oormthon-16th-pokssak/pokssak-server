package com.goormthon.controller.user

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.goormthon.controller.auth.request.AuthRequest
import com.goormthon.enums.AuthorityType
import com.goormthon.storage.rdb.user.UserJpaRepository
import com.goormthon.support.BaseIntegrationTest

@DisplayName("Simple UserController 테스트")
class SimpleUserControllerTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    @Test
    @DisplayName("단순 사용자 목록 조회 - ADMIN")
    fun simpleGetUsersTest() {
        // given - 사용자 생성
        val signupRequest =
            AuthRequest.Signup(
                name = "관리자",
                email = "admin@example.com",
                password = "password123!",
            )

        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)),
        )
            .andExpect(status().isCreated)

        // ADMIN으로 변경
        val user = userJpaRepository.findByEmail("admin@example.com")!!
        user.role = AuthorityType.ADMIN
        userJpaRepository.save(user)

        // 재로그인하여 새 토큰 발급
        val signinRequest =
            AuthRequest.Signin(
                email = "admin@example.com",
                password = "password123!",
            )

        val signinResult =
            mockMvc.perform(
                post("/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signinRequest)),
            )
                .andExpect(status().isOk)
                .andReturn()

        val response = objectMapper.readTree(signinResult.response.contentAsString)
        val accessToken = response.get("data").get("accessToken").asText()

        // when & then
        val getUsersResult =
            mockMvc.perform(
                get("/users")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                    .param("page", "0")
                    .param("size", "20"),
            )
                .andExpect(status().isOk)
                .andReturn()

        println("Response: ${getUsersResult.response.contentAsString}")
    }
}
