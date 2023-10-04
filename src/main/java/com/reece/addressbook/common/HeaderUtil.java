package com.reece.addressbook.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderUtil {
    public static String getUsername(HttpHeaders requestHeaders) {
        return requestHeaders.getFirst(HeaderNames.X_USERNAME);
    }

    public static String getCorrelationId(HttpHeaders requestHeaders) {
        return requestHeaders.getFirst(HeaderNames.X_CORRELATION_ID);
    }
}
