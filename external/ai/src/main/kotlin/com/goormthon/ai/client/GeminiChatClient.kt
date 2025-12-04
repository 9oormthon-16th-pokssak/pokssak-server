package com.goormthon.ai.client

import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.GenerateContentResponse
import com.google.gson.Gson
import com.goormthon.ai.config.GeminiProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class GeminiChatClient(
    private val geminiProperties: GeminiProperties,
) {
    private val logger = KotlinLogging.logger {}
    private val gson = Gson()
    private val client: Client =
        Client
            .builder()
            .apiKey(geminiProperties.apiKey)
            .build()

    fun generateContent(
        prompt: String,
        contents: String,
    ): String? {
        val fullPrompt = "상황: $prompt\n\n질문: $contents\n\n위 상황을 바탕으로 질문에 대해 답변해주세요."
        return try {
            logger.info { "Gemini API 호출 시작" }
            val response =
                client.models.generateContent(
                    geminiProperties.model,
                    fullPrompt,
                    GenerateContentConfig
                        .builder()
                        .temperature(0.4f) // 창의성과 정확성 밸런스
                        .maxOutputTokens(4000) // 15개 데이터라 토큰이 꽤 필요함
                        .responseMimeType("application/json")
                        .build(),
                )
            logger.info { "Gemini API 응답 받음" }
            getResponseText(response)
        } catch (e: Exception) {
            logger.error(e) { "Gemini API 호출 중 예외 발생" }
            null
        }
    }

    fun getResponseText(response: GenerateContentResponse?): String? {
        return try {
            if (response == null) {
                logger.warn { "GenerateContentResponse가 null입니다" }
                return null
            }

            val candidates = response.candidates()?.get()
            if (candidates.isNullOrEmpty()) {
                logger.warn { "응답 candidates가 비어있습니다" }
                return null
            }

            val content = candidates.first().content()?.get()
            if (content == null) {
                logger.warn { "응답 content가 null입니다" }
                return null
            }

            val text = content.text()
            if (text.isNullOrBlank()) {
                logger.warn { "응답 text가 비어있습니다" }
                return null
            }

            logger.info { "Gemini 원본 응답 (첫 500자): ${text.take(500)}" }

            // responseMimeType("application/json")을 사용하므로 text가 이미 JSON 문자열임
            // {"response": "..."} 형식이 아니라 직접 JSON 데이터를 반환
            text
        } catch (e: Exception) {
            logger.error(e) { "응답 파싱 중 예외 발생" }
            null
        }
    }
}
