package com.reece.addressbook.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidHeaderContentException extends RuntimeException {
    private final Map<String, String> headerMap;

    public InvalidHeaderContentException(Map<String, String> headerMap) {
        super("Invalid header content");
        this.headerMap = headerMap;
    }
}
