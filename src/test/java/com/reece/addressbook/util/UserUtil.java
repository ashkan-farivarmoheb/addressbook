package com.reece.addressbook.util;

import com.reece.addressbook.entity.User;

public final class UserUtil {

    public static User create() {
        return User.builder()
                .id(1l)
                .name("user")
                .build();
    }
}
