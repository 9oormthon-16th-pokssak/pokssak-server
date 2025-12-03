package com.goormthon.controller.auth

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.goormthon.controller.auth.request.AuthRequest
import com.goormthon.support.BaseIntegrationTest

@DisplayName("AuthController 통합 테스트")
class AuthControllerTest : BaseIntegrationTest() {
    @Test
    @DisplayName("회원가입 성공 - 정상적인 요청으로 회원가입을 한다")
    fun signupSuccess() {
        // given
        val request =
            AuthRequest.Signup(
                name = "홍길동",
                email = "test@example.com",
                password = "password123!",
            )

        // when & then
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.data.refreshToken").isNotEmpty)
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일로 가입 시도")
    fun signupFailWithDuplicateEmail() {
        // given
        val request =
            AuthRequest.Signup(
                name = "홍길동",
                email = "test@example.com",
                password = "password123!",
            )

        // 첫 번째 회원가입
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isCreated)

        // when & then - 동일 이메일로 재가입 시도
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    @DisplayName("로그인 성공 - 정상적인 이메일과 비밀번호로 로그인")
    fun signinSuccess() {
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

        val signinRequest =
            AuthRequest.Signin(
                email = "test@example.com",
                password = "password123!",
            )

        // when & then
        mockMvc.perform(
            post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signinRequest)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.data.refreshToken").isNotEmpty)
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    fun signinFailWithNonExistentEmail() {
        // given
        val request =
            AuthRequest.Signin(
                email = "nonexistent@example.com",
                password = "password123!",
            )

        // when & then
        mockMvc.perform(
            post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    fun signinFailWithWrongPassword() {
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

        val signinRequest =
            AuthRequest.Signin(
                email = "test@example.com",
                password = "wrongpassword!",
            )

        // when & then
        mockMvc.perform(
            post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signinRequest)),
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 요청 형식 (빈 이메일)")
    fun signinFailWithEmptyEmail() {
        // given
        val invalidRequest = """{"email": "", "password": "password123!"}"""

        // when & then
        mockMvc.perform(
            post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest),
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 요청 형식 (빈 비밀번호)")
    fun signupFailWithEmptyPassword() {
        // given
        val invalidRequest = """{"name": "홍길동", "email": "test@example.com", "password": ""}"""

        // when & then
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest),
        )
            .andExpect(status().is4xxClientError)
    }
}
