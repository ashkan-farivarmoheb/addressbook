package com.reece.addressbook.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum RequestHttpHeadersEnum {

    MANDATORY_HEADERS(Arrays.asList(HeaderNames.X_CORRELATION_ID, HeaderNames.X_USERNAME));

    private List<String> headers;
}
