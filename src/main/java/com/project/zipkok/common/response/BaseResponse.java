package com.project.zipkok.common.response;

        import com.fasterxml.jackson.annotation.JsonIgnore;
        import com.fasterxml.jackson.annotation.JsonInclude;
        import com.fasterxml.jackson.annotation.JsonPropertyOrder;
        import com.project.zipkok.common.response.status.BaseExceptionResponseStatus;
        import com.project.zipkok.common.response.status.ResponseStatus;
        import lombok.Getter;

        import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Getter
@JsonPropertyOrder({"code", "message", "result"})
public class BaseResponse<T> implements ResponseStatus {

    private final int code;

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    public BaseResponse(T result) {
        this.code = SUCCESS.getCode();
        this.message = SUCCESS.getMessage();
        this.result = result;
    }

    public BaseResponse(BaseExceptionResponseStatus baseExceptionResponseStatus, T result) {
        this.code = baseExceptionResponseStatus.getCode();
        this.message = baseExceptionResponseStatus.getMessage();
        this.result = result;
    }


    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
