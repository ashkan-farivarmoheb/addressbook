package com.reece.addressbook.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import reactor.core.CoreSubscriber;
import reactor.core.Scannable;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

@Configuration
@Slf4j
public class ReactorHook {

    public static final String MDC_SUBSCRIBER_CONTEXT_KEY = "MDC_SUBSCRIBER_CONTEXT_KEY";
    private static final String MDC_HOOK_OPERATOR = ReactorHook.class.getCanonicalName();

    @PostConstruct
    public void attachHook() {
        Hooks.onEachOperator(MDC_HOOK_OPERATOR, Operators.lift(new MdcContextHook<>()));
    }

    private static class MdcContextHook<T> implements BiFunction<Scannable, CoreSubscriber<? super T>, CoreSubscriber<? super T>> {
        @Override
        public CoreSubscriber<? super T> apply(Scannable scannable, CoreSubscriber<? super T> coreSubscriber) {
            return new MdcContextHookImpl<>(coreSubscriber);
        }

        static final class MdcContextHookImpl<U> implements CoreSubscriber<U> {
            private final CoreSubscriber<? super U> delegate;

            public MdcContextHookImpl(CoreSubscriber<? super U> delegate) {
                this.delegate = delegate;
            }

            @Override
            public Context currentContext() {
                return delegate.currentContext();
            }

            @Override
            public void onSubscribe(Subscription subscription) {
                delegate.onSubscribe(subscription);
            }

            @Override
            public void onNext(U element) {
                this.copyToMdc(delegate);
                delegate.onNext(element);
            }

            @Override
            public void onError(Throwable err) {
                this.copyToMdc(delegate);
                delegate.onError(err);
            }

            @Override
            public void onComplete() {
                this.copyToMdc(delegate);
                delegate.onComplete();
            }

            private void copyToMdc(CoreSubscriber<? super U> base) {
                if (!base.currentContext().isEmpty()) {
                    final Map<String, String> map = base.currentContext().getOrDefault(MDC_SUBSCRIBER_CONTEXT_KEY, Collections.EMPTY_MAP);
                    MDC.setContextMap(map);
                }
            }
        }
    }
}
