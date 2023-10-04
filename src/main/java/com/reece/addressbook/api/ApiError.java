package com.reece.addressbook.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private String errorId;
    private String message;
    private String informationLink;
    private List<ApiErrorDetails> details;

    public ApiError(HttpStatus status) {
        this.errorId = String.valueOf(status.value());
    }

    public ApiError(HttpStatus status, Throwable ex) {
        this(status);
        this.message = ex.getMessage();
    }

    public ApiError(HttpStatus status, Throwable ex, List<ApiErrorDetails> details) {
        this(status, ex);
        this.details = details;
    }
}
