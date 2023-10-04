package com.reece.addressbook.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestHeader {
    CORRELATION_ID("x-correlation-id", "correlationId"),
    USERNAME("x-username", "username");

    private final String name;
    private final String mdcKey;
}
