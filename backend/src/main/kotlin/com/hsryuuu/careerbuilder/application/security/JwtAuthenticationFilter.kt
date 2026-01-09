package com.hsryuuu.careerbuilder.application.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.net.URI

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: CustomUserDetailsService,
    private val redisTemplate: StringRedisTemplate
) : OncePerRequestFilter() {
    companion object {
        const val BEARER_PREFIX = "Bearer "
    }

    private val log = LoggerFactory.getLogger(this.javaClass)


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Authorization 헤더에서 JWT 토큰 추출
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        // "Bearer " 접두사 이후의 토큰 부분 추출
        val jwt = authHeader.substring(BEARER_PREFIX.length)

        // 로그아웃 확인 (블랙리스트 방식)
        if (isLogoutToken(jwt)) {
            filterChain.doFilter(request, response)
            return;
        }

        try {
            // 토큰에서 사용자 이름 추출
            //val username = jwtTokenProvider.getUsernameFromToken(jwt)
            val userInfo = jwtTokenProvider.getUserFromToken(jwt)

            // 인증 컨텍스트가 비어있는 경우에만 처리
            if (userInfo.username.isNotEmpty() && SecurityContextHolder.getContext().authentication == null) {
                // 사용자 상세 정보 로드
                val userDetails = userDetailsService.loadUserByUsername(userInfo.username)

                // 토큰 유효성 검증
                if (jwtTokenProvider.validateToken(jwt, userInfo)) {
                    // 인증 객체 생성
                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )

                    // 인증 객체에 요청 상세 정보 추가
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                    // 보안 컨텍스트에 인증 객체 설정
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (ex: ExpiredJwtException) {
            // 토큰 만료 시 TOKEN_EXPIRED 에러 응답 반환
            log.debug("토큰 만료: ${ex.message}")
            sendTokenExpiredResponse(response, request.requestURI)
            return
        } catch (ex: Exception) {
            // 기타 토큰 검증 실패 시 로깅
            logger.error("인증 설정 중 오류 발생: ${ex.message}")
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response)
    }

    private fun isLogoutToken(jwt: String): Boolean {
        try {
            val isBlacklisted = redisTemplate.hasKey("blacklist:$jwt")

            return isBlacklisted ?: false
        } catch (ex: Exception) {
            log.error("JWTAuthenticationFilter Redis Error", ex)
            return false;
        }
    }

    /**
     * 토큰 만료 시 RFC 7807 ProblemDetail 형식으로 응답
     */
    private fun sendTokenExpiredResponse(response: HttpServletResponse, requestUri: String) {
        val errorCode = ErrorCode.TOKEN_EXPIRED
        response.status = errorCode.status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val err = ProblemDetail.forStatusAndDetail(
            errorCode.status,
            errorCode.message
        ).apply {
            type = URI.create("https://careerbuilder.example.com/problems/validation-error")
            title = errorCode.name
            instance = URI.create(requestUri)
        }
        val problemDetailString = ObjectMapper().writeValueAsString(err)

        response.writer.write(problemDetailString)
    }
}