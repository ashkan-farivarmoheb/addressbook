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

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ComponentTest
public class AddressBookRepositoryTest {

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
    @Order(1)
    void findByNameAndUserName_validInput_shouldReturn() {
        User user1 = userRepository.save(User.builder().name("ashkan").build());
        addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());
        Optional<AddressBook> addressBook = addressBookRepository.findByNameAndUserName("melbourne", "ashkan");
        verifyAddressBookObject(addressBook, "melbourne", "ashkan");
    }

    @Test
    @Order(2)
    void findByIdAndUserId_validInput_shouldReturn() {
        User user1 = userRepository.save(User.builder().name("ashkan").build());
        AddressBook addressBook1 = addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());
        Optional<AddressBook> addressBook = addressBookRepository.findByIdAndUserId(addressBook1.getId(), user1.getId());
        verifyAddressBookObject(addressBook, "melbourne", "ashkan");
    }

    @Test
    @Order(3)
    void findByIdAndUserIdAndContactsIn_validInput_shouldReturn() {
        User ava = userRepository.save(User.builder().name("ava").build());
        AddressBook friends = addressBookRepository.save(AddressBook.builder().user(ava).name("friends").build());

        Contact contact1 = contactRepository.save(Contact.builder().addressBook(friends).firstname("ashkan").lastname("farivarmoheb").company("abc").number("+61(02)89886551").build());

        Optional<AddressBook> addressBook = addressBookRepository.findByIdAndUserIdAndContactsIn(friends.getId(), ava.getId(), Arrays.asList(contact1.getId()));
        verifyAddressBookObject(addressBook, "friends", "ava");
    }

    @Test
    @Order(4)
    void findWithContactsByIdAndUserIdAndContactsIn_validInput_shouldReturn() {
        User ava = userRepository.save(User.builder().name("ava").build());
        AddressBook friends = addressBookRepository.save(AddressBook.builder().user(ava).name("friends").build());

        Contact contact1 = contactRepository.save(Contact.builder().addressBook(friends).firstname("ashkan").lastname("farivarmoheb").company("abc").number("+61(02)89886551").build());

        Optional<AddressBook> addressBook = addressBookRepository.findWithContactsByIdAndUserIdAndContactsIn(friends.getId(), ava.getId(), Arrays.asList(contact1.getId()));
        verifyAddressBookObject(addressBook, "friends", "ava");
        assertNotNull(addressBook.get().getContacts());
        assertEquals(addressBook.get().getContacts().size(), 1);
    }

    @Test
    @Order(5)
    void findByUserId_validInput_shouldReturn() {
        User user1 = userRepository.save(User.builder().name("ashkan").build());
        AddressBook addressBook1 = addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());
        List<AddressBook> addressBook = addressBookRepository.findByUserId(user1.getId());

        assertNotNull(addressBook);
        assertEquals(addressBook.size(), 1);
        verifyAddressBookObject(Optional.of(addressBook.get(0)), "melbourne", "ashkan");
    }

    private static void verifyAddressBookObject(Optional<AddressBook> addressBook, String name, String username) {
        assertNotNull(addressBook);
        assertNotNull(addressBook.get().getId());
        assertNotNull(addressBook.get().getUser().getId());
        assertEquals(addressBook.get().getName(), name);
        assertEquals(addressBook.get().getUser().getName(), username);
        verifyAuditingObject(addressBook);
    }

    private static void verifyAuditingObject(Optional<AddressBook> addressBook) {
        assertEquals(addressBook.get().getCreatedBy(), "AUDITOR");
        assertEquals(addressBook.get().getLastModifiedBy(), "AUDITOR");
        assertNotNull(addressBook.get().getCreatedDate());
        assertNotNull(addressBook.get().getLastModifiedDate());
    }

}
