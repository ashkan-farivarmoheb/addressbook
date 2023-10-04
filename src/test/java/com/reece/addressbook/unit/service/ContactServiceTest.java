package com.reece.addressbook.unit.service;

import com.reece.addressbook.api.CreateContactRequest;
import com.reece.addressbook.api.CreateContactResponse;
import com.reece.addressbook.api.GetContactRequest;
import com.reece.addressbook.api.GetContactResponse;
import com.reece.addressbook.api.GetContactsResponse;
import com.reece.addressbook.entity.Contact;
import com.reece.addressbook.repository.ContactRepository;
import com.reece.addressbook.service.ContactService;
import com.reece.addressbook.service.mapper.ContactMapper;
import com.reece.addressbook.util.ContactUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ContactServiceTest {

    @InjectMocks
    private ContactService contactService;

    @Mock
    private ContactMapper contactMapper;

    @Mock
    private ContactRepository contactRepository;

    @Test
    void create_validRequest_shouldSuccess() {
        Contact contact = ContactUtil.create();
        when(contactMapper.toContact(any(CreateContactRequest.class))).thenReturn(contact);
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);
        when(contactMapper.toCreateContactResponse(any(Contact.class))).thenReturn(ContactUtil.createContactResponse());

        ArgumentCaptor<Contact> argumentCaptor = ArgumentCaptor.forClass(Contact.class);
        ArgumentCaptor<Contact> argumentCaptorResponse = ArgumentCaptor.forClass(Contact.class);
        CreateContactResponse response = contactService.create(ContactUtil.createContactRequest()).block();

        verify(contactMapper).toContact(any(CreateContactRequest.class));
        verify(contactRepository).save(argumentCaptor.capture());
        assertEquals("firstname", argumentCaptor.getValue().getFirstname());
        assertEquals("lastname", argumentCaptor.getValue().getLastname());
        assertNull(argumentCaptor.getValue().getId());
        verify(contactMapper).toCreateContactResponse(argumentCaptorResponse.capture());
        assertEquals("firstname", argumentCaptorResponse.getValue().getFirstname());
        assertEquals("lastname", argumentCaptorResponse.getValue().getLastname());
        assertEquals("firstname", response.getFirstname());
        assertEquals("lastname", response.getLastname());
        assertEquals("user", response.getUsername());
        assertEquals("name", response.getAddressName());
    }

    @Test
    void getContacts_validRequest_shouldSuccess() {
        GetContactRequest request = GetContactRequest.builder().username("user").addressId(1l).pageable(PageRequest.of(0, 3)).build();
        List<Contact> contacts = ContactUtil.getContacts(3);
        when(contactRepository.findByAddressBookId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(contacts, PageRequest.of(0, 3), 3));
        when(contactRepository.countByAddressBookId(anyLong())).thenReturn(3l);
        when(contactMapper.toGetContactResponse(any(Contact.class))).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            return GetContactResponse.builder()
                    .firstname(contact.getFirstname()).lastname(contact.getLastname()).company(contact.getCompany()).type(contact.getType()).phoneNumber(contact.getNumber())
                    .build();
        });

        Page<GetContactsResponse> response = contactService.getContacts(request).block();
        assertEquals(3, response.getContent().get(0).getContacts().size());
        assertEquals("user", response.getContent().get(0).getUsername());
        assertEquals(1l, response.getContent().get(0).getAddressId());
        assertEquals(3, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        verify(contactRepository).findByAddressBookId(anyLong(), any(Pageable.class));
        verify(contactRepository).countByAddressBookId(anyLong());
        verify(contactMapper, times(3)).toGetContactResponse(any(Contact.class));
    }

    @Test
    void getAllContacts_validRequest_shouldSuccess() {
        GetContactRequest request = GetContactRequest.builder().username("user").addressId(1l).pageable(PageRequest.of(0, 3)).build();
        List<Contact> contacts = ContactUtil.getContacts(3);
        when(contactRepository.findByAddressBookUserName(anyString(), any(Pageable.class))).thenReturn(new PageImpl<>(contacts, PageRequest.of(0, 3), 3));
        when(contactRepository.countByAddressBookUserName(anyString())).thenReturn(3l);
        when(contactMapper.toGetContactResponse(any(Contact.class))).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            return GetContactResponse.builder()
                    .firstname(contact.getFirstname()).lastname(contact.getLastname()).company(contact.getCompany()).type(contact.getType()).phoneNumber(contact.getNumber())
                    .build();
        });

        Page<GetContactsResponse> response = contactService.getAllContacts(request).block();
        assertEquals(3, response.getContent().get(0).getContacts().size());
        assertEquals("user", response.getContent().get(0).getUsername());
        assertEquals(1l, response.getContent().get(0).getAddressId());
        assertEquals(3, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        verify(contactRepository).findByAddressBookUserName(anyString(), any(Pageable.class));
        verify(contactRepository).countByAddressBookUserName(anyString());
        verify(contactMapper, times(3)).toGetContactResponse(any(Contact.class));
    }

    @Test
    void delete_validContactId_shouldSuccess() {
        doNothing().when(contactRepository).deleteById(anyLong());
        contactService.delete(1l);
    }

}
