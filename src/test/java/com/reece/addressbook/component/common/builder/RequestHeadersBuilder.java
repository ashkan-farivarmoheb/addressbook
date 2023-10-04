package com.reece.addressbook.component.common.builder;

import com.reece.addressbook.common.HeaderNames;
import org.springframework.http.HttpHeaders;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class RequestHeadersBuilder {
    public static HttpHeaders defaultRequestHeaders(String correlationId, String username) {
        return createRequestHeaders(correlationId, username);
    }

    private static HttpHeaders createRequestHeaders(String correlationId, String username) {
        HttpHeaders headers = new HttpHeaders();

        if (isNotBlank(correlationId)) {
            headers.set(HeaderNames.X_CORRELATION_ID, correlationId);
        }

        if (isNotBlank(username)) {
            headers.set(HeaderNames.X_USERNAME, username);
        }

        return headers;
    }
}
