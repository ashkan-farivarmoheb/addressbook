package com.reece.addressbook.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class ProblemDetailBuilder {

    private ProblemDetail problemDetail;

    public static ProblemDetailBuilder builder() {
        return new ProblemDetailBuilder();
    }

    public ProblemDetailBuilder status(HttpStatus httpStatus) {
        problemDetail = ProblemDetail.forStatus(httpStatus.value());
        return this;
    }

    public ProblemDetailBuilder detail(String message) {
        problemDetail.setDetail(message);
        return this;
    }

    public ProblemDetailBuilder title(String title) {
        problemDetail.setTitle(title);
        return this;
    }

    public ProblemDetailBuilder type(URI type) {
        problemDetail.setType(type);
        return this;
    }

    public ProblemDetailBuilder property(String name, @Nullable Object value) {
        problemDetail.setProperty(name, value);
        return this;
    }

    public ProblemDetail build() {
        return problemDetail;
    }
}
