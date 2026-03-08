package com.project.dugoga.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    AUTHORIZATION(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "요청이 올바르지 않습니다."),
    CONFLICT(HttpStatus.CONFLICT, "요청이 서버의 상태와 충돌했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),
    TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 가게
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND,  "존재하지 않는 가게입니다."),
    STORE_NOT_OPEN(HttpStatus.BAD_REQUEST, "현재 영업 중인 가게가 아닙니다."),
    STORE_ALREADY_DELETED(HttpStatus.NOT_FOUND, "이미 삭제된 가게입니다."),
    STORE_INVALID_OPERATING_HOURS(HttpStatus.BAD_REQUEST, "오픈 시간은 마감 시간보다 빨라야 합니다."),
    STORE_NOT_SERVICE_AREA(HttpStatus.NOT_FOUND, "현재 가게 위치는 서비스 제공 지역이 아닙니다."),
    STORE_NOT_OWNER(HttpStatus.FORBIDDEN, "본인이 등록한 가게 정보만 관리 가능합니다."),

    // 상품
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND,  "존재하지 않는 상품입니다."),
    PRODUCT_HIDDEN(HttpStatus.NOT_FOUND,  "현재 주문할 수 없는 상품입니다."),
    PRODUCT_SOLD_OUT(HttpStatus.CONFLICT,  "품절된 상품이 포함되어 있습니다."),
    PRODUCT_ALREADY_DELETED(HttpStatus.NOT_FOUND, "이미 삭제된 상품입니다."),
    PRODUCT_NOT_OWNER(HttpStatus.FORBIDDEN, "본인이 등록한 가게의 상품만 관리 가능합니다."),

    // 주문
    ORDER_PRODUCTS_REQUIRED(HttpStatus.BAD_REQUEST, "주문할 상품을 한 개 이상 선택해 주세요."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
    ORDER_CANCEL_NOT_ALLOWED_STATUS(HttpStatus.BAD_REQUEST, "현재 주문 상태에서는 취소할 수 없습니다."),
    ORDER_ACCEPT_NOT_ALLOWED_STATUS(HttpStatus.NOT_FOUND, "현재 주문 상태에서는 수락할 수 없습니다."),
    ORDER_REJECT_NOT_ALLOWED_STATUS(HttpStatus.NOT_FOUND, "현재 주문 상태에서는 거절할 수 없습니다."),
    ORDER_CANCEL_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "주문 취소 가능 시간이 지났습니다."),
    ORDER_ALREADY_CANCELLED(HttpStatus.NOT_FOUND, "이미 취소된 주문입니다."),
    ORDER_ALREADY_ACCEPTED(HttpStatus.NOT_FOUND, "이미 수락된 주문입니다."),
    ORDER_ALREADY_REJECTED(HttpStatus.NOT_FOUND, "이미 거절된 주문입니다."),

    // 카테고리
    DUPLICATE_CATEGORY_CODE(HttpStatus.CONFLICT, "이미 존재하는 카테고리 코드입니다." ),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "이미 존재하는 카테고리 이름입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    CATEGORY_ALREADY_DELETED(HttpStatus.CONFLICT, "삭제된 카테고리입니다."),
    CATEGORY_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "카테고리 이름은 필수입니다."),
    CATEGORY_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "카테고리 코드는 필수입니다."),
    CATEGORY_NOT_DELETED(HttpStatus.BAD_REQUEST, "삭제되지 않은 카테고리입니다." ),

    // 유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    EXISTS_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    EXISTS_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 별명입니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_NOT_OWNER(HttpStatus.FORBIDDEN, "점주 권한이 필요합니다."),

    // 서비스 가능 지역
    AVAILABLE_ADDRESS_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 서비스 지역입니다."),
    AVAILABLE_ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 서비스 지역을 찾을 수 없습니다." ),

    // 즐겨찾기
    BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 즐겨찾기입니다." ),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 즐겨찾기입니다."),
    BOOKMARK_ALREADY_DELETED(HttpStatus.NOT_FOUND, "이미 삭제된 즐겨찾기입니다." ),
    BOOKMARK_NOT_DELETED(HttpStatus.CONFLICT,"삭제된 상태의 즐겨찾만 복구할 수 있습니다." ),

    // AI_PROMPT
    AI_PROMPT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 AI 상품 설명입니다."),

    // Image (File Format Error)
    INVALID_FILE_TYPE(HttpStatus.NOT_FOUND, "지원하지 않는 파일 형식입니다."),
    FILE_URL_NOT_FOUND(HttpStatus.NOT_FOUND, "S3 URL을 찾을 수 없습니다."),
    DOMAIN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 도메인입니다."),

    // 리뷰
    INAPPROPRIATE_REVIEW(HttpStatus.UNPROCESSABLE_ENTITY, "부적절한 내용이 포함되어 있습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.NOT_FOUND, "이미 리뷰가 존재합니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다.");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }
}
