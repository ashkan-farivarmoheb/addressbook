package com.reece.addressbook.common;

import java.util.concurrent.Callable;

public class MdcAwareCallableService implements CallableService {
    public MdcAwareCallableService() {
    }

    @Override
    public <T> Callable<T> wrap(Callable<T> callable) {
        return new MdcAwareCallableDecorator<>(callable);
    }
}
