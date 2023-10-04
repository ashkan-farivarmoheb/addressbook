package com.reece.addressbook.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

import static com.reece.addressbook.common.HeaderNames.X_CORRELATION_ID;
import static org.apache.commons.lang3.StringUtils.length;

@Slf4j
public enum HeaderRestrictions {

    CORRELATION_ID(X_CORRELATION_ID, 36, 36,
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"); //12aaa456-3bb4-b31a-b31a-b31abbbaaa34

    @Getter
    private String headerName;
    private int minLength;
    @Getter
    private int maxLength;
    private Optional<String> regex;

    HeaderRestrictions(String headerName, int minLength, int maxLength, String regexString) {
        this.headerName = headerName;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.regex = Optional.ofNullable(regexString);
    }

    public boolean validate(String headerValue, Map<String, String> invalidHeaderMap) {
        boolean invalid = false;

        int headerLength = length(headerValue);
        if (headerLength > maxLength) {
            log.debug("message=\"Header exceeded maximum length\", header_name=\"{}\", min_length=\"{}\"",
                    headerName, maxLength);
            invalid = true;
        }

        if (headerLength < minLength) {
            log.debug("message=\"Header exceeded minimum length\", header_name=\"{}\", min_length=\"{}\"",
                    headerName, minLength);
            invalid = true;
        }

        if (failsRegexRestriction(headerValue)) {
            log.debug("message=\"Header fails regular expression\", header_name=\"{}\"",
                    headerName);
            invalid = true;
        }
        return invalid;
    }

    private boolean failsRegexRestriction(String headerValue) {
        if (!regex.isPresent()) {
            return false;
        }
        return null == headerValue || !headerValue.matches(regex.get());
    }
}
