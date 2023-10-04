package com.reece.addressbook.exception;

import java.util.Arrays;

public class MandatoryHeaderMissingException extends RuntimeException {
    private final String[] headerNames;

    public MandatoryHeaderMissingException(String... headerNames) {
        super("Following mandatory headers are missing: " + Arrays.toString((headerNames)));
        this.headerNames = headerNames;
    }

    public String[] getHeaderNames() {
        return this.headerNames;
    }
}
