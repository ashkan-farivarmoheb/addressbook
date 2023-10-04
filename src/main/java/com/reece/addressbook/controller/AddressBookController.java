package com.reece.addressbook.controller;

import com.reece.addressbook.api.CreateAddressBookRequest;
import com.reece.addressbook.api.CreateAddressBookResponse;
import com.reece.addressbook.api.CreateContactRequest;
import com.reece.addressbook.api.CreateContactResponse;
import com.reece.addressbook.api.DeleteContactRequest;
import com.reece.addressbook.api.GetContactRequest;
import com.reece.addressbook.api.GetContactsResponse;
import com.reece.addressbook.common.CallableService;
import com.reece.addressbook.service.AddressBookService;
import com.reece.addressbook.validation.ValidRequestHeader;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

import static com.reece.addressbook.common.HeaderUtil.getUsername;
import static com.reece.addressbook.common.RequestHttpHeadersEnum.MANDATORY_HEADERS;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class AddressBookController {
    private final AddressBookService addressBookService;
    private final CallableService callableService;

    @PostMapping("/v1/addressbooks")
    public Callable<ResponseEntity<CreateAddressBookResponse>> createAddress(
            @RequestBody @Valid final CreateAddressBookRequest request,
            @RequestHeader @ValidRequestHeader(mandatoryHeaders = MANDATORY_HEADERS) final HttpHeaders requestHeaders) {
        return callableService.wrap(() -> {
            String username = getUsername(requestHeaders);
            request.setUsername(username);

            CreateAddressBookResponse response = addressBookService.createAddress(request).block();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        });
    }

    @PostMapping("/v1/addressbooks/{addressId}/contacts")
    @ResponseStatus(HttpStatus.CREATED)
    public Callable<ResponseEntity<CreateContactResponse>> createContact(
            @PathVariable final Long addressId,
            @RequestBody @Valid final CreateContactRequest request,
            @RequestHeader @ValidRequestHeader(mandatoryHeaders = MANDATORY_HEADERS) final HttpHeaders requestHeaders) {
        return callableService.wrap(() -> {
            String username = getUsername(requestHeaders);
            request.setUsername(username);
            request.setAddressId(addressId);

            CreateContactResponse response = addressBookService.createContact(request).block();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        });
    }

    @GetMapping("/v1/addressbooks/{addressId}/contacts")
    public Callable<ResponseEntity<Page<GetContactsResponse>>> getContacts(
            @PathVariable final Long addressId,
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "firstname", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "lastname", direction = Sort.Direction.ASC)
            }) Pageable pageable,
            @RequestHeader @ValidRequestHeader(mandatoryHeaders = MANDATORY_HEADERS) final HttpHeaders requestHeaders) {
        return callableService.wrap(() -> {
            String username = getUsername(requestHeaders);
            GetContactRequest request = GetContactRequest.builder()
                    .addressId(addressId).username(username).pageable(pageable).build();
            Page<GetContactsResponse> response = addressBookService.getContacts(request).block();
            return new ResponseEntity<>(response, HttpStatus.OK);
        });
    }

    @GetMapping("/v1/addressbooks/contacts")
    public Callable<ResponseEntity<Page<GetContactsResponse>>> getAllContacts(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "firstname", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "lastname", direction = Sort.Direction.ASC)
            }) Pageable pageable,
            @RequestHeader @ValidRequestHeader(mandatoryHeaders = MANDATORY_HEADERS) final HttpHeaders requestHeaders) {
        return callableService.wrap(() -> {
            String username = getUsername(requestHeaders);
            GetContactRequest request = GetContactRequest.builder().username(username).pageable(pageable).build();
            Page<GetContactsResponse> response = addressBookService.getAllContacts(request).block();
            return new ResponseEntity<>(response, HttpStatus.OK);
        });
    }

    @DeleteMapping("/v1/addressbooks/{addressId}/contact/{contactId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Callable<ResponseEntity<HttpStatus>> deleteContact(
            @PathVariable final Long addressId,
            @PathVariable final Long contactId,
            @RequestHeader @ValidRequestHeader(mandatoryHeaders = MANDATORY_HEADERS) final HttpHeaders requestHeaders) {
        return callableService.wrap(() -> {
            String username = getUsername(requestHeaders);
            DeleteContactRequest request = DeleteContactRequest.builder()
                    .addressId(addressId).contactId(contactId).username(username).build();
            addressBookService.deleteContact(request);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        });
    }
}
