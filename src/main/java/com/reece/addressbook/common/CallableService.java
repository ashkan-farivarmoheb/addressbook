package com.reece.addressbook.common;

import java.util.concurrent.Callable;

public interface CallableService {
    <T> Callable<T> wrap(Callable<T> var1);
}
