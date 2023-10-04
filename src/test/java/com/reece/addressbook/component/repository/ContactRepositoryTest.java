package com.reece.addressbook.component.repository;

import com.reece.addressbook.component.common.ComponentTest;
import com.reece.addressbook.entity.AddressBook;
import com.reece.addressbook.entity.Contact;
import com.reece.addressbook.entity.User;
import com.reece.addressbook.repository.AddressBookRepository;
import com.reece.addressbook.repository.ContactRepository;
import com.reece.addressbook.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ComponentTest
public class ContactRepositoryTest {

    @Autowired
    private AddressBookRepository addressBookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;


    @BeforeEach
    public void setup() {
        contactRepository.deleteAll();
        addressBookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    public void cleanup() {
        contactRepository.deleteAll();
        addressBookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByAddressBookId_validInput_shouldReturn() {
        User ava = userRepository.save(User.builder().name("ava").build());
        AddressBook friends = addressBookRepository.save(AddressBook.builder().user(ava).name("friends").build());

        Contact contact1 = contactRepository.save(Contact.builder().addressBook(friends).firstname("ashkan").lastname("farivarmoheb").company("abc").number("+61(02)89886551").build());
        Contact contact2 = contactRepository.save(Contact.builder().addressBook(friends).firstname("f1").lastname("l1").company("abc").number("+61(02)89886552").build());

        Page<Contact> contacts = contactRepository.findByAddressBookId(friends.getId(), PageRequest.of(1, 1, Sort.Direction.ASC, "firstname"));
        assertNotNull(contacts);
        assertEquals(contacts.getTotalElements(), 2);
        assertEquals(contacts.getTotalPages(), 2);
        assertEquals(contacts.get().toList().get(0).getFirstname(), "f1");
        assertEquals(contacts.get().toList().get(0).getLastname(), "l1");
        assertEquals(contacts.get().toList().get(0).getCompany(), "abc");
        assertEquals(contacts.get().toList().get(0).getNumber(), "+61(02)89886552");
    }

    @Test
    void findByAddressBookUserName_validInput_shouldReturn() {
        User ava = userRepository.save(User.builder().name("ava").build());
        AddressBook friends = addressBookRepository.save(AddressBook.builder().user(ava).name("friends").build());

        Contact contact1 = contactRepository.save(Contact.builder().addressBook(friends).firstname("ashkan").lastname("farivarmoheb").company("abc").number("+61(02)89886551").build());
        Contact contact2 = contactRepository.save(Contact.builder().addressBook(friends).firstname("f1").lastname("l1").company("abc").number("+61(02)89886552").build());

        Page<Contact> contacts = contactRepository.findByAddressBookUserName(ava.getName(), PageRequest.of(1, 1, Sort.Direction.ASC, "firstname"));
        assertNotNull(contacts);
        assertEquals(contacts.getTotalElements(), 2);
        assertEquals(contacts.getTotalPages(), 2);
        assertEquals(contacts.get().toList().get(0).getFirstname(), "f1");
        assertEquals(contacts.get().toList().get(0).getLastname(), "l1");
        assertEquals(contacts.get().toList().get(0).getCompany(), "abc");
        assertEquals(contacts.get().toList().get(0).getNumber(), "+61(02)89886552");
    }
}
