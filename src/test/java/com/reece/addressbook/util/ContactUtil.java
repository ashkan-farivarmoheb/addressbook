package com.reece.addressbook.util;

import com.reece.addressbook.api.CreateContactRequest;
import com.reece.addressbook.api.CreateContactResponse;
import com.reece.addressbook.api.GetContactResponse;
import com.reece.addressbook.api.GetContactsResponse;
import com.reece.addressbook.entity.Contact;
import com.reece.addressbook.entity.PhoneType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ContactUtil {

    public static Contact create() {
        return create("firstname", "lastname", "company", "number");
    }

    public static Contact create(String firstname, String lastname, String company, String number) {
        return Contact.builder()
                .firstname(firstname)
                .lastname(lastname)
                .company(company)
                .number(number)
                .type(PhoneType.HOME)
                .addressBook(AddressBookUtil.create())
                .build();
    }

    public static CreateContactRequest createContactRequest() {
        return CreateContactRequest.builder()
                .firstname("firstname")
                .lastname("lastname")
                .company("company")
                .phoneNumber("number")
                .type(PhoneType.HOME)
                .addressId(1l)
                .username("user")
                .build();
    }

    public static CreateContactRequest createContactRequest(String phoneNumber) {
        return CreateContactRequest.builder()
                .firstname("firstname")
                .lastname("lastname")
                .company("company")
                .phoneNumber(phoneNumber)
                .type(PhoneType.HOME)
                .addressId(1l)
                .username("user")
                .build();
    }

    public static CreateContactResponse createContactResponse() {
        return CreateContactResponse.builder()
                .firstname("firstname")
                .lastname("lastname")
                .company("company")
                .phoneNumber("number")
                .type(PhoneType.HOME)
                .addressName("name")
                .username("user")
                .build();
    }

    public static List<Contact> getContacts(int items) {
        return IntStream.range(0, items)
                .mapToObj(String::valueOf)
                .map(i -> create("firstname".concat(i), "lastname".concat(i), "company".concat(i), "number".concat(i)))
                .collect(Collectors.toList());
    }

    public static Set<GetContactResponse> getContactResponses(int items) {
        return IntStream.range(0, items)
                .mapToObj(String::valueOf)
                .map(i -> GetContactResponse.builder().firstname("firstname".concat(i)).lastname("lastname".concat(i)).company("company".concat(i)).phoneNumber("number".concat(i)).build())
                .collect(Collectors.toSet());
    }

    public static GetContactsResponse createGetContactsResponse(int items) {
        return GetContactsResponse.builder().contacts(getContactResponses(items)).username("user").addressId(1l).build();
    }
}
