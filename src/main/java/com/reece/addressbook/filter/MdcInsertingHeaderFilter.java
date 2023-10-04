package com.reece.addressbook.filter;


import com.reece.addressbook.common.RequestHeader;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@Order(2)
public class MdcInsertingHeaderFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        insertIntoMdc(request);
        try {
            chain.doFilter(request, response);
        } finally {
            clearMdc();
        }
    }

    private void insertIntoMdc(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        Arrays.stream(RequestHeader.values()).forEach(header -> {
            String headerValue = httpRequest.getHeader(header.getName());
            if (isNotBlank(headerValue)) {
                MDC.put(header.getMdcKey(), headerValue);
            }
        });
    }


    private void clearMdc() {
        MDC.clear();
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
