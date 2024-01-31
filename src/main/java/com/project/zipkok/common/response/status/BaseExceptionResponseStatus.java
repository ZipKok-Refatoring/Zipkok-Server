package com.project.zipkok.common.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BaseExceptionResponseStatus implements ResponseStatus {
    /**
     * 1000: 요청 성공 (OK)
     */
    SUCCESS(1000, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /**
     * 2000: Request 예외 (BAD_REQUEST)
     */
    BAD_REQUEST(2000, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 요청입니다."),
    URL_NOT_FOUND(2001, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 URL 입니다."),
    METHOD_NOT_ALLOWED(2002, HttpStatus.METHOD_NOT_ALLOWED.value(), "해당 URL에서는 지원하지 않는 HTTP Method 입니다."),

    /**
     * 3000: Server, Database 예외 (INTERNAL_SERVER_ERROR)
     */
    SERVER_ERROR(3000, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에서 오류가 발생하였습니다."),
    DATABASE_ERROR(3001, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스에서 오류가 발생하였습니다."),
    BAD_SQL_GRAMMAR(3002, HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL에 오류가 있습니다."),

    /**
     * 4000: Authorization 예외
     */
    JWT_ERROR(4000, HttpStatus.UNAUTHORIZED.value(), "JWT에서 오류가 발생하였습니다."),
    TOKEN_NOT_FOUND(4001, HttpStatus.BAD_REQUEST.value(), "토큰이 HTTP Header에 없습니다."),
    UNSUPPORTED_TOKEN_TYPE(4002, HttpStatus.BAD_REQUEST.value(), "지원되지 않는 토큰 형식입니다."),
    INVALID_TOKEN(4003, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),
    MALFORMED_TOKEN(4004, HttpStatus.UNAUTHORIZED.value(), "토큰이 올바르게 구성되지 않았습니다."),
    EXPIRED_TOKEN(4005, HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다."),
    TOKEN_MISMATCH(4006, HttpStatus.UNAUTHORIZED.value(), "로그인 정보가 토큰 정보와 일치하지 않습니다."),

    INVALID_REFRESH_TOKEN(4011, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 refreshToken입니다."),
    /**
     * 5000: User 예외
     */
    KAKAO_LOGIN_MEMBER_EXIST(5000, HttpStatus.OK.value(), "성공적으로 로그인 되었습니다."),
    KAKAO_LOGIN_NEED_REGISTRATION(5001, HttpStatus.OK.value(), "회원가입이 필요합니다."),
    REGISTRATION_SUCCESS(5002, HttpStatus.OK.value(), "회원가입 성공"),
    MEMBER_INFO_UPDATE_SUCCESS(5003, HttpStatus.OK.value(), "회원정보 등록/수정 성공"),
    MY_PAGE_INFO_LOAD_SUCCESS(5004, HttpStatus.OK.value(), "마이페이지 회원정보 로드 성공"),
    MY_PAGE_DETAIL_UPDATE_SUCCESS(5005, HttpStatus.OK.value(), "마이페이지 세부항목 수정 성공"),
    MEMBER_FILTER_UPDATE_SUCCESS(5006, HttpStatus.OK.value(), "회원 필터 정보 수정 성공"),
    KAKAO_AUTH_FAILURE(5007, HttpStatus.UNAUTHORIZED.value(), "카카오 인증 실패"),
    INVALID_NICKNAME_FORMAT(5008, HttpStatus.BAD_REQUEST.value(), "닉네임이 없거나 잘못된 형식"),
    INVALID_GENDER_FORMAT(5009, HttpStatus.BAD_REQUEST.value(), "성별이 없거나 잘못된 형식"),
    INVALID_BIRTHDAY_FORMAT(5010, HttpStatus.BAD_REQUEST.value(), "생년월일이 없거나 잘못된 형식"),
    ADDRESS_OVER_LENGTH(5011, HttpStatus.BAD_REQUEST.value(), "주소가 없거나 최대 입력 범위 초과"),
    INVALID_LAT_LNG(5012, HttpStatus.BAD_REQUEST.value(), "위도, 경도가 없거나 범위 초과"),
    INVALID_MIN_PRICE(5013, HttpStatus.BAD_REQUEST.value(), "최소 가격이 없거나 0 이하"),
    INVALID_MAX_PRICE(5014, HttpStatus.BAD_REQUEST.value(), "최대 가격이 없거나 범위 초과"),
    MEMBER_NOT_FOUND(5015, HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원"),
    HIGHLIGHT_ALREADY_EXIST(5016, HttpStatus.BAD_REQUEST.value(), "이미 존재하는 highlight"),
    INVALID_OPTION_ORDER(5017, HttpStatus.BAD_REQUEST.value(), "option 순서 오류"),
    MISSING_OPTION_INFO(5018, HttpStatus.BAD_REQUEST.value(), "option 정보 누락"),
    MISSING_DETAIL_OPTION_INFO(5019, HttpStatus.BAD_REQUEST.value(), "detailOpion 정보 누락"),
    INVALID_INTEREST_TYPE(5020, HttpStatus.BAD_REQUEST.value(), "관심매물유형 오류"),
    MEMBER_LIST_ITEM_QUERY_SUCCESS(5021, HttpStatus.OK.value(), "회원 리스트 항목 조회 성공"),
    MEMBER_LIST_ITEM_QUERY_FAILURE(5022, HttpStatus.BAD_REQUEST.value(), "회원 리스트 항목 조회 실패"),
    MEMBER_LIST_ITEM_UPDATE_SUCCESS(5023, HttpStatus.OK.value(), "회원 리스트 항목 수정 성공"),
    MEMBER_LIST_ITEM_UPDATE_FAILURE(5024, HttpStatus.BAD_REQUEST.value(), "회원 리스트 항목 수정 실패"),
    MIN_IS_BIGGER_THAN_MAX(5025, HttpStatus.BAD_REQUEST.value(), "최솟값은 최댓값을 넘을 수 없습니다."),
    INVALID_EMAIL_REQUEST(5026, HttpStatus.BAD_REQUEST.value(), "잘못된 이메일 형식입니다."),

    /**
     * 6000 : zim 예외
     */
    // 찜 관련 예외
    FAVORITES_ADD_SUCCESS(6000, HttpStatus.OK.value(), "찜 등록 성공"),
    FAVORITES_QUERY_SUCCESS(6001, HttpStatus.OK.value(), "찜 조회 성공"),
    FAVORITES_CANCEL_SUCCESS(6002, HttpStatus.OK.value(), "찜 취소 성공"),
    FAVORITES_ADD_FAILURE(6003, HttpStatus.BAD_REQUEST.value(), "찜 등록 실패"),
    FAVORITES_QUERY_FAILURE(6004, HttpStatus.BAD_REQUEST.value(), "찜 조회 실패"),
    FAVORITES_CANCEL_FAILURE(6005, HttpStatus.BAD_REQUEST.value(), "찜 취소 실패"),
    ALREADY_EXIST_ZIM(6006, HttpStatus.BAD_REQUEST.value(), "이미 존재하는 찜입니다."),
    NO_EXIST_ZIM(6007, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 찜입니다."),


    /**
     * 7000 : kok 예외
     */
    KOK_LIST_QUERY_SUCCESS(7001, HttpStatus.OK.value(), "콕리스트 조회 성공"),
    KOK_DETAIL_QUERY_SUCCESS(7002, HttpStatus.OK.value(), "콕 세부조회 성공"),
    KOK_NEIGHBORHOOD_INFO_QUERY_SUCCESS(7003, HttpStatus.OK.value(), "콕의 집 주변 정보 조회 성공"),
    KOK_INTERNAL_INFO_QUERY_SUCCESS(7004, HttpStatus.OK.value(), "콕의 집 내부 정보 조회 성공"),
    KOK_CONTRACT_INFO_QUERY_SUCCESS(7005, HttpStatus.OK.value(), "콕의 중개 계약 정보 조회 성공"),
    KOK_REVIEW_INFO_QUERY_SUCCESS(7006, HttpStatus.OK.value(), "콕의 리뷰 정보 조회 성공"),
    KOK_ID_NOT_FOUND(7007, HttpStatus.NOT_FOUND.value(), "kokId에 해당하는 정보를 찾을 수 없습니다."),
    NO_MORE_KOK_DATA(7008, HttpStatus.OK.value(), "더이상 조회할 콕 데이터가 없습니다. (무한 스크롤 끝)"),
    MEMBER_SETTING_INFO_QUERY_SUCCESS(7009, HttpStatus.OK.value(), "회원 설정 정보 조회 성공"),
    MEMBER_SETTING_INFO_QUERY_FAILURE(7010, HttpStatus.BAD_REQUEST.value(), "회원 설정 정보 조회 실패"),
    KOK_REGISTRATION_SUCCESS(7011, HttpStatus.OK.value(), "콕 등록 성공"),
    KOK_REGISTRATION_FAILURE(7012, HttpStatus.BAD_REQUEST.value(), "콕 등록 실패"),

    /**
     * 8000 : RealEstate 예외
     */
    PROPERTY_REGISTRATION_SUCCESS(8000, HttpStatus.OK.value(), "매물 등록 성공"),
    PROPERTY_DETAIL_QUERY_SUCCESS(8001, HttpStatus.OK.value(), "매물 상세 정보 조회 성공"),
    PROPERTY_MAP_QUERY_SUCCESS(8002, HttpStatus.OK.value(), "지도 상 매물 정보 조회 성공"),
    PROPERTY_REGISTRATION_FAILURE(8003, HttpStatus.BAD_REQUEST.value(), "매물 등록에 실패"),
    PROPERTY_NOT_FOUND(8004, HttpStatus.NOT_FOUND.value(), "해당 주소에 매물이 존재하지 않음"),
    INVALID_PROPERTY_NAME(8005, HttpStatus.BAD_REQUEST.value(), "매물이름 정보가 없거나 형식이 잘못됨"),
    INVALID_RENTAL_PRICE_FORMAT(8006, HttpStatus.BAD_REQUEST.value(), "보증금/월세/매매가 정보가 없거나 형식이 잘못됨"),
    INVALID_MANAGEMENT_FEE_FORMAT(8007, HttpStatus.BAD_REQUEST.value(), "관리비 정보가 없거나 형식이 잘못됨"),
    INVALID_ADDRESS_FORMAT(8008, HttpStatus.BAD_REQUEST.value(), "주소 정보가 없거나 형식이 잘못됨"),
    INVALID_PROPERTY_ID(8009, HttpStatus.NOT_FOUND.value(), "존재하지 않는 매물이거나 잘못된 매물 id"),
    INVALID_LATITUDE_FORMAT(8010, HttpStatus.BAD_REQUEST.value(), "위도 정보가 없거나 형식이 잘못됨"),
    INVALID_LONGITUDE_FORMAT(8011, HttpStatus.BAD_REQUEST.value(), "경도 정보가 없거나 형식이 잘못됨"),
    INVALID_REALESTATE_ID(8012, HttpStatus.BAD_REQUEST.value(), "매물 아이디 형식이 잘못됨"),


    /**
     * 9000 : Address 예외
     */
    ADDRESS_SEARCH_SUCCESS(9000, HttpStatus.OK.value(), "주소 검색 성공"),

    ADDRESS_SEARCH_FAILURE(9001, HttpStatus.NOT_FOUND.value(), "주소 검색 실패");


    private final int code;
    private final int status;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
