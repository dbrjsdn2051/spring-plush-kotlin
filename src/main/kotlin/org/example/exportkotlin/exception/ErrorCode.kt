package org.example.exportkotlin.exception

enum class ErrorCode(
    val status: Int,
    val message: String,
) {
    USER_NOT_FOUND(404, "해당 유저를 찾을 수 없습니다."),
    VALID_ENCODE_PASSWORD_EQUAL(400, "새 비밀번호는 기존 비밀번호와 같을 수 없습니다."),
    PASSWORD_MISSMATCH(400, "잘못된 비밀번호입니다."),
    TODO_NOT_FOUND(404, "일정을 찾을 수 없습니다."),
    WEATHER_NOT_FOUND(404, "날씨 정보를 찾을 수 없습니다."),
    INVALID_TODO_MANAGER(400, "일정을 만든 유저가 아닙니다."),
    AUTHOR_CANNOT_ASSIGN_SELF(400, "본인이 작성한 일정은 담당자로 지정할 수 없습니다."),
    INVALID_TODO_USER(400, "해당 일정을 만든 유저가 유효하지 않습니다."),
    TODO_FORBIDDEN_MANAGER(400, "해당 일정에 등록된 담당자가 아닙니다."),
    MANAGER_NOT_FOUND(404, "매니저를 찾을 수 없습니다."),
    UNKNOWN_AUTHENTICATED(401, "인증 정보가 잘못되었습니다."),
    TOKEN_NOT_FOUND(401, "토큰 정보가 없습니다."),
    EXISTS_EMAIL(400, "존재하는 이메일 입니다."),
}