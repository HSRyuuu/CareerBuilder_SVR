package com.hsryuuu.careerbuilder.domain.ai.model.type

enum class AiRequestType {
    EXPERIENCE_ANALYSIS, // 경험 분석
    RESUME,     // 이력서 코칭
    CAREER,     // 커리어 분석
    INTERVIEW_GEN        // 면접 질문 생성
}

enum class ReferenceType {
    EXPERIENCES, // 경험 tableName: experiences
}

enum class AiRequestStatus {
    PENDING,    // 대기
    PROCESSING, // 처리 중
    SUCCESS,    // 성공
    FAILURE     // 실패
}
