package com.reece.addressbook.exception;

import java.util.Map;

public class InvalidParameterException extends RuntimeException {
    private final Map<String, String> parameterMap;

    public InvalidParameterException(Map<String, String> parameterMap) {
        super("Invalid parameters: " + parameterMap);
        this.parameterMap = parameterMap;
    }

    public Map<String, String> getParameterMap() {
        return this.parameterMap;
    }
}
