package com.reece.addressbook.component.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reece.addressbook.api.CreateAddressBookRequest;
import com.reece.addressbook.component.common.ComponentTest;
import com.reece.addressbook.component.common.builder.RequestHeadersBuilder;
import com.reece.addressbook.entity.AddressBook;
import com.reece.addressbook.entity.Contact;
import com.reece.addressbook.entity.User;
import com.reece.addressbook.repository.AddressBookRepository;
import com.reece.addressbook.repository.ContactRepository;
import com.reece.addressbook.repository.UserRepository;
import com.reece.addressbook.util.AddressBookUtil;
import com.reece.addressbook.util.ContactUtil;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentTest
@AutoConfigureMockMvc
public class AddressBookControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private AddressBookRepository addressBookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    private ObjectMapper mapper = new ObjectMapper();

    private static final String CREATE_ADDRESS_BOOK_V1_PATH = "/v1/addressbooks";
    private static final String CREATE_ADDRESS_BOOK_V2_PATH = "/v2/addressbooks";
    private static final String CREATE_CONTACT_V1_PATH = "/v1/addressbooks/{addressId}/contacts";
    private static final String DELETE_CONTACT_V1_PATH = "/v1/addressbooks/{addressId}/contact/{contactId}";
    private static final String GET_CONTACTS_V1_PATH = "/v1/addressbooks/{addressId}/contacts";
    private static final String GET_ALL_CONTACTS_V1_PATH = "/v1/addressbooks/contacts";

    private static HttpHeaders httpHeaders;

    @BeforeEach
    public void setup() {
        httpHeaders = RequestHeadersBuilder.defaultRequestHeaders("12aaa456-3bb4-b31a-b31a-b31abbbaaa34", "ashkan");
        userRepository.deleteAll();
        addressBookRepository.deleteAll();
        contactRepository.deleteAll();
    }

    @Test
    public void createAddress_validRequest_success() throws Exception {
        User user1 = userRepository.save(User.builder().name("ashkan").build());

        MvcResult result =
                mvc.perform(post(CREATE_ADDRESS_BOOK_V1_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .headers(httpHeaders)
                                .content(mapper.writeValueAsString(AddressBookUtil.createAddressBookRequest())))
                        .andExpect(request().asyncStarted())
                        .andReturn();
        mvc.perform(asyncDispatch(result))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void createContact_validRequest_success() throws Exception {
        User user1 = userRepository.save(User.builder().name("ashkan").build());
        AddressBook addressBook1 = addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());

        mvc.perform(post(CREATE_CONTACT_V1_PATH, addressBook1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders)
                        .content(mapper.writeValueAsString(ContactUtil.createContactRequest("61(02)89886551"))))
                .andExpect(status().isCreated());
    }

    @Test
    public void getContacts_validRequest_success() throws Exception {
        User user1 = userRepository.save(User.builder().name("ashkan").build());
        AddressBook addressBook1 = addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());
        Contact contact1 = contactRepository.save(Contact.builder().addressBook(addressBook1).firstname("ashkan").lastname("farivarmoheb").company("abc").number("+61(02)89886551").build());

        MvcResult result =
                mvc.perform(get(GET_CONTACTS_V1_PATH, addressBook1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .headers(httpHeaders))
                        .andExpect(request().asyncStarted())
                        .andReturn();
        mvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getAllContacts_validRequest_success() throws Exception {
        User user1 = userRepository.save(User.builder().name("ashkan").build());
        AddressBook addressBook1 = addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());
        Contact contact1 = contactRepository.save(Contact.builder().addressBook(addressBook1).firstname("ashkan").lastname("farivarmoheb").company("abc").number("+61(02)89886551").build());

        AddressBook addressBook2 = addressBookRepository.save(AddressBook.builder().user(user1).name("sydney").build());
        Contact contact2 = contactRepository.save(Contact.builder().addressBook(addressBook2).firstname("ashkan").lastname("farivarmoheb").company("abc").number("+61(02)89886551").build());
        Contact contact3 = contactRepository.save(Contact.builder().addressBook(addressBook2).firstname("ashkan1").lastname("farivarmoheb").company("abc").number("+61(02)89886551").build());


        MvcResult result =
                mvc.perform(get(GET_ALL_CONTACTS_V1_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .headers(httpHeaders))
                        .andExpect(request().asyncStarted())
                        .andReturn();
        mvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].contacts", hasSize(2)))
                .andExpect(jsonPath("$.content[0].contacts[0].firstname", Is.is("ashkan")))
                .andExpect(jsonPath("$.content[0].contacts[1].firstname", Is.is("ashkan1")));

    }

    @Test
    public void getAllContacts_validRequestNoContact_success() throws Exception {
        User user1 = userRepository.save(User.builder().name("ashkan").build());
        AddressBook addressBook1 = addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());
        AddressBook addressBook2 = addressBookRepository.save(AddressBook.builder().user(user1).name("sydney").build());

        MvcResult result =
                mvc.perform(get(GET_ALL_CONTACTS_V1_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .headers(httpHeaders))
                        .andExpect(request().asyncStarted())
                        .andReturn();
        mvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].contacts", hasSize(0)));

    }

    @Test
    public void deleteContact_validRequest_success() throws Exception {
        User user1 = userRepository.save(User.builder().name("ashkan").build());
        AddressBook addressBook1 = addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());
        Contact contact1 = contactRepository.save(Contact.builder().addressBook(addressBook1).firstname("ashkan").lastname("farivarmoheb").company("abc").number("+61(02)89886551").build());

        mvc.perform(delete(DELETE_CONTACT_V1_PATH, addressBook1.getId(), contact1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders))
                .andExpect(status().isNoContent());
    }

    @Test
    public void createContact_invalidPhoneNumberRequest_failure400() throws Exception {
        User user1 = userRepository.save(User.builder().name("ashkan").build());
        AddressBook addressBook1 = addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());

        mvc.perform(post(CREATE_CONTACT_V1_PATH, addressBook1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders)
                        .content(mapper.writeValueAsString(ContactUtil.createContactRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("detail", Is.is("Invalid request content.")));
    }

    @Test
    public void createAddress_invalidVersion_failure400() throws Exception {
        User user1 = userRepository.save(User.builder().name("ashkan").build());
        addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());

        mvc.perform(post(CREATE_ADDRESS_BOOK_V2_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders)
                        .content(mapper.writeValueAsString(AddressBookUtil.createAddressBookRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createAddress_missingHeaders_failure400() throws Exception {

        HttpHeaders httpHeaders = RequestHeadersBuilder.defaultRequestHeaders(null, "ashkan");

        User user1 = userRepository.save(User.builder().name("ashkan").build());
        AddressBook addressBook1 = addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());

        mvc.perform(post(CREATE_ADDRESS_BOOK_V1_PATH, addressBook1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders)
                        .content(mapper.writeValueAsString(AddressBookUtil.createAddressBookRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("detail", Is.is("Following mandatory headers are missing: [x-correlation-id]")));
    }

    @Test
    public void createAddress_invalidContentHeaders_failure400() throws Exception {

        HttpHeaders httpHeaders = RequestHeadersBuilder.defaultRequestHeaders("5478rytfh", "ashkan");

        User user1 = userRepository.save(User.builder().name("ashkan").build());
        AddressBook addressBook1 = addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());

        mvc.perform(post(CREATE_ADDRESS_BOOK_V1_PATH, addressBook1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders)
                        .content(mapper.writeValueAsString(AddressBookUtil.createAddressBookRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("detail", Is.is("Invalid header content")));
    }

    @Test
    public void createAddress_invalidRequest_failure400() throws Exception {

        User user1 = userRepository.save(User.builder().name("ashkan").build());
        AddressBook addressBook1 = addressBookRepository.save(AddressBook.builder().user(user1).name("melbourne").build());

        mvc.perform(post(CREATE_ADDRESS_BOOK_V1_PATH, addressBook1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders)
                        .content(mapper.writeValueAsString(CreateAddressBookRequest.builder().build())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("detail", Is.is("Invalid request content.")));
    }
}
