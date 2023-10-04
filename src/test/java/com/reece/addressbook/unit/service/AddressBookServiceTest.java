package com.reece.addressbook.unit.service;

import com.reece.addressbook.api.CreateAddressBookRequest;
import com.reece.addressbook.api.CreateAddressBookResponse;
import com.reece.addressbook.api.CreateContactRequest;
import com.reece.addressbook.api.DeleteContactRequest;
import com.reece.addressbook.api.GetContactRequest;
import com.reece.addressbook.api.GetContactsResponse;
import com.reece.addressbook.entity.AddressBook;
import com.reece.addressbook.entity.User;
import com.reece.addressbook.exception.ServiceException;
import com.reece.addressbook.repository.AddressBookRepository;
import com.reece.addressbook.service.AddressBookService;
import com.reece.addressbook.service.ContactService;
import com.reece.addressbook.service.UserService;
import com.reece.addressbook.service.mapper.AddressBookMapper;
import com.reece.addressbook.util.AddressBookUtil;
import com.reece.addressbook.util.ContactUtil;
import com.reece.addressbook.util.UserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AddressBookServiceTest {
    @InjectMocks
    private AddressBookService addressBookService;
    @Mock
    private AddressBookMapper addressBookMapper;
    @Mock
    private AddressBookRepository addressBookRepository;
    @Mock
    private UserService userService;
    @Mock
    private ContactService contactService;

    @Test
    void createAddress_validRequest_shouldSuccess() {
        CreateAddressBookRequest request = AddressBookUtil.createAddressBookRequest();
        when(addressBookRepository.findByNameAndUserName(anyString(), anyString())).thenReturn(Optional.empty());
        when(userService.getUserByName(anyString())).thenReturn(Mono.just(UserUtil.create()));
        when(addressBookMapper.toAddressBook(any(CreateAddressBookRequest.class), any(User.class))).thenAnswer(invocation -> {
            CreateAddressBookRequest createAddressBookRequest = invocation.getArgument(0);
            User user = invocation.getArgument(1);
            return AddressBook.builder()
                    .name(createAddressBookRequest.getAddressBookName())
                    .user(User.builder().id(user.getId()).name(user.getName()).build()).build();
        });
        when(addressBookRepository.save(any(AddressBook.class))).thenAnswer(invocation -> {
            AddressBook addressBook = invocation.getArgument(0);
            addressBook.setId(1l);
            return addressBook;
        });
        when(addressBookMapper.toCreateAddressBookResponse(any(AddressBook.class))).thenAnswer(invocation -> {
            AddressBook addressBook = invocation.getArgument(0);
            return CreateAddressBookResponse.builder().name(addressBook.getName()).user(addressBook.getUser().getName()).build();
        });

        StepVerifier.create(addressBookService.createAddress(request))
                .thenConsumeWhile(fetchedObject -> {
                    verify(addressBookRepository).findByNameAndUserName(anyString(), anyString());
                    verify(userService).getUserByName(anyString());
                    verify(addressBookMapper).toAddressBook(any(CreateAddressBookRequest.class), any(User.class));
                    verify(addressBookRepository).save(any(AddressBook.class));
                    verify(addressBookMapper).toCreateAddressBookResponse(any(AddressBook.class));
                    assertNotNull(fetchedObject);
                    Assertions.assertEquals(fetchedObject.getName(), "name");
                    Assertions.assertEquals(fetchedObject.getUser(), "user");
                    return true;
                }).verifyComplete();
    }

    @Test
    void createAddress_addressExists_shouldFailure() {
        CreateAddressBookRequest request = CreateAddressBookRequest.builder().addressBookName("name").username("user").build();
        when(addressBookRepository.findByNameAndUserName(anyString(), anyString())).thenReturn(Optional.ofNullable(AddressBookUtil.create()));
        StepVerifier.create(addressBookService.createAddress(request))
                .consumeErrorWith(ex -> {
                    assertThat(ex, is(instanceOf(ServiceException.class)));
                    assertEquals("This address book exists", ex.getMessage());
                }).verify();
    }

    @Test
    void createAddress_invalidUser_shouldFailure() {
        CreateAddressBookRequest request = AddressBookUtil.createAddressBookRequest();
        when(addressBookRepository.findByNameAndUserName(anyString(), anyString())).thenReturn(Optional.empty());
        when(userService.getUserByName(anyString())).thenReturn(Mono.error(new ServiceException("Invalid user")));
        StepVerifier.create(addressBookService.createAddress(request))
                .consumeErrorWith(ex -> {
                    assertThat(ex, is(instanceOf(ServiceException.class)));
                    assertEquals("Invalid user", ex.getMessage());
                }).verify();
    }

    @Test
    void createContact_invalidUser_validRequest_shouldSuccess() {
        CreateContactRequest request = ContactUtil.createContactRequest();
        when(userService.getUserByName(anyString())).thenReturn(Mono.just(UserUtil.create()));
        when(addressBookRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(AddressBookUtil.create()));
        when(contactService.create(any(CreateContactRequest.class))).thenReturn(Mono.just(ContactUtil.createContactResponse()));

        StepVerifier.create(addressBookService.createContact(request)).thenConsumeWhile(fetchedObject -> {
            verify(addressBookRepository).findByIdAndUserId(anyLong(), anyLong());
            verify(userService).getUserByName(anyString());
            verify(contactService).create(any(CreateContactRequest.class));
            assertNotNull(fetchedObject);
            Assertions.assertEquals(fetchedObject.getAddressName(), "name");
            Assertions.assertEquals(fetchedObject.getUsername(), "user");
            Assertions.assertEquals(fetchedObject.getFirstname(), "firstname");
            Assertions.assertEquals(fetchedObject.getLastname(), "lastname");
            return true;
        }).verifyComplete();

    }

    @Test
    void createContact_invalidUser_shouldFailure() {
        CreateContactRequest request = ContactUtil.createContactRequest();
        when(userService.getUserByName(anyString())).thenReturn(Mono.error(new ServiceException("Invalid user")));
        StepVerifier.create(addressBookService.createContact(request))
                .consumeErrorWith(ex -> {
                    assertThat(ex, is(instanceOf(ServiceException.class)));
                    assertEquals("Invalid user", ex.getMessage());
                }).verify();
    }

    @Test
    void createContact_addressNotExists_shouldFailure() {
        CreateContactRequest request = ContactUtil.createContactRequest();
        when(addressBookRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(userService.getUserByName(anyString())).thenReturn(Mono.just(UserUtil.create()));
        StepVerifier.create(addressBookService.createContact(request))
                .consumeErrorWith(ex -> {
                    assertThat(ex, is(instanceOf(ServiceException.class)));
                    assertEquals("Invalid address book", ex.getMessage());
                }).verify();
    }

    @Test
    void getContacts_validRequest_shouldSuccess() {
        GetContactRequest request = GetContactRequest.builder().username("user").addressId(1l).build();
        Page<GetContactsResponse> contactsResponse = new PageImpl<>(Arrays.asList(ContactUtil.createGetContactsResponse(3)), PageRequest.of(0, 3), 3);

        when(addressBookRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(AddressBookUtil.create()));
        when(userService.getUserByName(anyString())).thenReturn(Mono.just(UserUtil.create()));
        when(contactService.getContacts(any(GetContactRequest.class))).thenReturn(Mono.just(contactsResponse));
        StepVerifier.create(addressBookService.getContacts(request)).thenConsumeWhile(fetchedObject -> {
            verify(addressBookRepository).findByIdAndUserId(anyLong(), anyLong());
            verify(userService).getUserByName(anyString());
            verify(contactService).getContacts(any(GetContactRequest.class));
            assertNotNull(fetchedObject);
            assertEquals(3, fetchedObject.getContent().get(0).getContacts().size());
            assertEquals("user", fetchedObject.getContent().get(0).getUsername());
            assertEquals(1l, fetchedObject.getContent().get(0).getAddressId());
            assertEquals(3, fetchedObject.getTotalElements());
            assertEquals(1, fetchedObject.getTotalPages());
            return true;
        }).verifyComplete();
    }

    @Test
    void getContacts_invalidUser_shouldFailure() {
        GetContactRequest request = GetContactRequest.builder().username("user").addressId(1l).build();
        when(userService.getUserByName(anyString())).thenReturn(Mono.error(new ServiceException("Invalid user")));
        StepVerifier.create(addressBookService.getContacts(request))
                .consumeErrorWith(ex -> {
                    assertThat(ex, is(instanceOf(ServiceException.class)));
                    assertEquals("Invalid user", ex.getMessage());
                }).verify();
    }

    @Test
    void getContacts_addressNotExists_shouldFailure() {
        GetContactRequest request = GetContactRequest.builder().username("user").addressId(1l).build();
        when(addressBookRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(userService.getUserByName(anyString())).thenReturn(Mono.just(UserUtil.create()));
        StepVerifier.create(addressBookService.getContacts(request))
                .consumeErrorWith(ex -> {
                    assertThat(ex, is(instanceOf(ServiceException.class)));
                    assertEquals("Invalid address book", ex.getMessage());
                }).verify();
    }

    @Test
    void deleteContact_invalidUser_shouldFailure() {
        DeleteContactRequest request = DeleteContactRequest.builder().username("user").addressId(1l).contactId(1l).build();
        when(userService.getUserByName(anyString())).thenThrow(new ServiceException("Invalid user"));
        ServiceException serviceException = assertThrows(ServiceException.class, () -> addressBookService.deleteContact(request));
        assertAll(() -> {
            assertThat(serviceException, is(instanceOf(ServiceException.class)));
            assertEquals("Invalid user", serviceException.getMessage());
        });
    }

    @Test
    void deleteContact_validRequest_shouldSuccess() {
        DeleteContactRequest request = DeleteContactRequest.builder().username("user").addressId(1l).contactId(1l).build();
        when(userService.getUserByName(anyString())).thenReturn(Mono.just(UserUtil.create()));
        when(addressBookRepository.findByIdAndUserIdAndContactsIn(anyLong(), anyLong(), any())).thenReturn(Optional.ofNullable(AddressBookUtil.create()));
        doNothing().when(contactService).delete(request.getContactId());
        addressBookService.deleteContact(request);
        verify(userService).getUserByName(anyString());
        verify(addressBookRepository).findByIdAndUserIdAndContactsIn(anyLong(), anyLong(), any());
        verify(contactService).delete(request.getContactId());
    }


    @Test
    void getAllContacts_validRequest_shouldSuccess() {
        GetContactRequest request = GetContactRequest.builder().username("user").addressId(1l).build();
        Page<GetContactsResponse> contactsResponse = new PageImpl<>(Arrays.asList(ContactUtil.createGetContactsResponse(3)), PageRequest.of(0, 3), 3);

        when(addressBookRepository.findByUserId(anyLong())).thenReturn(Arrays.asList(AddressBookUtil.create()));
        when(userService.getUserByName(anyString())).thenReturn(Mono.just(UserUtil.create()));
        when(contactService.getAllContacts(any(GetContactRequest.class))).thenReturn(Mono.just(contactsResponse));
        StepVerifier.create(addressBookService.getAllContacts(request)).thenConsumeWhile(fetchedObject -> {
            verify(addressBookRepository).findByUserId(anyLong());
            verify(userService).getUserByName(anyString());
            verify(contactService).getAllContacts(any(GetContactRequest.class));
            assertNotNull(fetchedObject);
            assertEquals(3, fetchedObject.getContent().get(0).getContacts().size());
            assertEquals("user", fetchedObject.getContent().get(0).getUsername());
            assertEquals(1l, fetchedObject.getContent().get(0).getAddressId());
            assertEquals(3, fetchedObject.getTotalElements());
            assertEquals(1, fetchedObject.getTotalPages());
            return true;
        }).verifyComplete();
    }

    @Test
    void getAllContacts_invalidUser_shouldFailure() {
        GetContactRequest request = GetContactRequest.builder().username("user").addressId(1l).build();
        when(userService.getUserByName(anyString())).thenReturn(Mono.error(new ServiceException("Invalid user")));
        StepVerifier.create(addressBookService.getAllContacts(request))
                .consumeErrorWith(ex -> {
                    assertThat(ex, is(instanceOf(ServiceException.class)));
                    assertEquals("Invalid user", ex.getMessage());
                }).verify();
    }

    @Test
    void getAllContacts_addressNotExists_shouldFailure() {
        GetContactRequest request = GetContactRequest.builder().username("user").addressId(1l).build();
        when(addressBookRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(userService.getUserByName(anyString())).thenReturn(Mono.just(UserUtil.create()));
        StepVerifier.create(addressBookService.getAllContacts(request))
                .consumeErrorWith(ex -> {
                    assertThat(ex, is(instanceOf(ServiceException.class)));
                    assertEquals("No Address book exists", ex.getMessage());
                }).verify();
    }
}
