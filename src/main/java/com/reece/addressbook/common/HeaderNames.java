package com.reece.addressbook.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderNames {
    public static final String X_CORRELATION_ID = "x-correlation-id";
    public static final String X_USERNAME = "x-username";
}
