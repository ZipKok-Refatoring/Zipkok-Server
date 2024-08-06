package com.project.zipkok.common.exception_handler;

import com.project.zipkok.common.exception.NoExistUserException;
import com.project.zipkok.common.exception.user.*;
import com.project.zipkok.common.response.BaseExceptionResponse;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.common.response.FieldErrorDetail;
import com.project.zipkok.common.response.ValidationErrorResponse;
import com.project.zipkok.common.response.status.BaseExceptionResponseStatus;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class UserExceptionControllerAdvice {

    private static final Map<String, BaseExceptionResponseStatus> FIELD_ERROR_MAP = createFieldErrorMap();

    private static Map<String, BaseExceptionResponseStatus> createFieldErrorMap() {
        Map<String, BaseExceptionResponseStatus> fieldErrorMap = new HashMap<>();
        // 회원 가입 api에 대한 field
        fieldErrorMap.put("nickname", INVALID_NICKNAME_FORMAT);
        fieldErrorMap.put("email", INVALID_EMAIL_REQUEST);
        fieldErrorMap.put("gender", INVALID_GENDER_FORMAT);
        fieldErrorMap.put("birthday", INVALID_BIRTHDAY_FORMAT);

        // 온보딩 정보 입력 api에 대한 field
        fieldErrorMap.put("address", ADDRESS_OVER_LENGTH);
        fieldErrorMap.put("latitude", INVALID_LAT_LNG);
        fieldErrorMap.put("longitude", INVALID_LAT_LNG);
        fieldErrorMap.put("mpriceMin", INVALID_MIN_PRICE);
        fieldErrorMap.put("mdepositMin", INVALID_MIN_PRICE);
        fieldErrorMap.put("ydepositMin", INVALID_MIN_PRICE);
        fieldErrorMap.put("purchaseMin", INVALID_MIN_PRICE);
        fieldErrorMap.put("mpriceMax", INVALID_MAX_PRICE);
        fieldErrorMap.put("mdepositMax", INVALID_MAX_PRICE);
        fieldErrorMap.put("ydepositMax", INVALID_MAX_PRICE);
        fieldErrorMap.put("purchaseMax", INVALID_MAX_PRICE);
        fieldErrorMap.put("realEstateType", INVALID_INTEREST_TYPE);
        fieldErrorMap.put("transactionType", INVALID_TRANSACTION_TYPE);
        fieldErrorMap.put("smallerthanMax", MIN_IS_BIGGER_THAN_MAX);

        return fieldErrorMap;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NoExistUserException.class)
    public BaseResponse handle_NoExistUserException(NoExistUserException e) {
        log.error("[handle_UserException]", e);
        return new BaseResponse(e.getExceptionStatus(), e.getGetLoginResponse());
    }

    private List<FieldErrorDetail> processFieldErrors(MethodArgumentNotValidException e) {
        List<FieldErrorDetail> fieldErrors = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldName = fieldError.getField();
            log.info("[processFieldName] {}", fieldName);
            BaseExceptionResponseStatus errorStatus = FIELD_ERROR_MAP.getOrDefault(fieldName, INVALID_FIELD_FORMAT);
            fieldErrors.add(new FieldErrorDetail(fieldName, errorStatus.getCode(), errorStatus.getMessage()));
        });
        return fieldErrors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse handle_UserValidationException(MethodArgumentNotValidException e) {
        log.error("[handle_UserValidationException]", e);
        List<FieldErrorDetail> fieldErrors = processFieldErrors(e);
        fieldErrors.forEach(fieldErrorDetail -> {
            log.info("[fieldName, message, code] {} {} {}", fieldErrorDetail.getField(), fieldErrorDetail.getCode(), fieldErrorDetail.getMessage());
        });

        return new ValidationErrorResponse(INVALID_FIELD_FORMAT, fieldErrors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserBadRequestException.class)
    public BaseExceptionResponse handle_UserBadRequestException(UserBadRequestException e) {
        log.error("[handle_UserBadRequestException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OnBoardingBadRequestException.class)
    public BaseExceptionResponse handle_OnBoardingBadRequestException(OnBoardingBadRequestException e) {
        log.error("[handle_OnBoardingBadRequestException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus(), e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BaseExceptionResponse handle_HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("[handle_HttpMessageNotReadableException]", e);
        return new BaseExceptionResponse(BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoMatchUserException.class)
    public BaseExceptionResponse handle_NoMatchUserException(NoMatchUserException e) {
        log.error("[handle_NoMatchUserException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());

    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(KokOptionLoadException.class)
    public BaseExceptionResponse handle_KokOptionLoadException(KokOptionLoadException e){
        log.error("[handle_KokOptionLoadException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus(), e.getMessage());
    }

}
