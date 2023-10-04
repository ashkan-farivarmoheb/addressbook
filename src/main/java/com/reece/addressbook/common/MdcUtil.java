package com.reece.addressbook.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MdcUtil {

    public static String getCorrelationId() {
        return MDC.get(RequestHeader.CORRELATION_ID.getMdcKey());
    }

    public static String getUsername() {
        return MDC.get(RequestHeader.USERNAME.getMdcKey());
    }
}
