package com.reece.addressbook.util;

import com.reece.addressbook.api.CreateAddressBookRequest;
import com.reece.addressbook.entity.AddressBook;

public class AddressBookUtil {

    public static AddressBook create() {
        return AddressBook.builder()
                .id(1l)
                .name("name")
                .user(UserUtil.create())
                .build();
    }

    public static CreateAddressBookRequest createAddressBookRequest() {
        return CreateAddressBookRequest.builder().addressBookName("name").username("user").build();
    }
}
