package com.reece.addressbook.common;

import lombok.Generated;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

@Getter
public class MdcAwareCallableDecorator<T> implements Callable<T> {

    @Generated
    private static final Logger log = LoggerFactory.getLogger(MdcAwareCallableDecorator.class);

    private final Callable<T> callable;

    private final Map contextMap = MDC.getCopyOfContextMap();

    public MdcAwareCallableDecorator(Callable<T> callable) {
        this.callable = callable;
        log.trace("this=\"{}\", current_thread_name=\"{}\", context_map=\"{}\"", new Object[]{this, Thread.currentThread().getName(), this.contextMap});
    }

    @Override
    public T call() throws Exception {
        if (this.contextMap != null) {
            MDC.setContextMap(this.contextMap);
            log.trace("this=\"{}\", current_thread_name=\"{}\", context_map=\"{}\"", new Object[]{this, Thread.currentThread().getName(), this.contextMap});
        } else {
            log.error("message=\"MDC contextMap is null. {} objects must have at least the correlation ID\"", this.getClass().getName());
        }

        Object var1;
        try {
            var1 = this.callable.call();
        } finally {
            MDC.clear();
        }
        return (T) var1;
    }

    public String toString() {
        String var1 = Integer.toHexString(this.hashCode());
        return "{ \"MDCAwareCallable@" + var1 + "\": { \"contextMap\": \"" + this.contextMap + "\", \"callable\": \"" + this.callable + "\"} }";
    }
}
