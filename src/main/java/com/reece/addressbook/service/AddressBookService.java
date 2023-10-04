package com.reece.addressbook.service;

import com.reece.addressbook.api.CreateAddressBookRequest;
import com.reece.addressbook.api.CreateAddressBookResponse;
import com.reece.addressbook.api.CreateContactRequest;
import com.reece.addressbook.api.CreateContactResponse;
import com.reece.addressbook.api.DeleteContactRequest;
import com.reece.addressbook.api.GetContactRequest;
import com.reece.addressbook.api.GetContactsResponse;
import com.reece.addressbook.entity.AddressBook;
import com.reece.addressbook.exception.ServiceException;
import com.reece.addressbook.repository.AddressBookRepository;
import com.reece.addressbook.service.mapper.AddressBookMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.reece.addressbook.common.MdcUtil.getCorrelationId;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressBookService {
    private final AddressBookMapper addressBookMapper;
    private final AddressBookRepository addressBookRepository;
    private final UserService userService;
    private final ContactService contactService;

    public Mono<CreateAddressBookResponse> createAddress(CreateAddressBookRequest request) {
        log.info("message=\"Calling createAddress service\", correlation_id=\"{}\"", getCorrelationId());

        return validateAddressBookNotExists(request)
                .flatMap(addressBook -> userService.getUserByName(request.getUsername()))
                .flatMap(user -> Mono.just(addressBookMapper.toAddressBook(request, user)))
                .flatMap(addressBook -> Mono.just(addressBookRepository.save(addressBook)))
                .flatMap(addressBook -> Mono.just(addressBookMapper.toCreateAddressBookResponse(addressBook)));
    }

    public Mono<CreateContactResponse> createContact(CreateContactRequest request) {
        log.info("message=\"Calling createContact service\", correlation_id=\"{}\"", getCorrelationId());

        return userService.getUserByName(request.getUsername())
                .flatMap(user -> validateAddressBookExists(request.getAddressId(), user.getId()))
                .flatMap(user -> contactService.create(request));
    }

    public Mono<Page<GetContactsResponse>> getContacts(GetContactRequest request) {
        log.info("message=\"Calling getContacts service\", correlation_id=\"{}\"", getCorrelationId());

        return userService.getUserByName(request.getUsername())
                .flatMap(user -> validateAddressBookExists(request.getAddressId(), user.getId()))
                .flatMap(addressBook -> contactService.getContacts(request));
    }

    public Mono<Page<GetContactsResponse>> getAllContacts(GetContactRequest request) {
        log.info("message=\"Calling getAllContacts service\", correlation_id=\"{}\"", getCorrelationId());

        return userService.getUserByName(request.getUsername())
                .flatMap(user -> validateAddressBookExists(user.getId()))
                .flatMap(addressBooks -> contactService.getAllContacts(request));
    }

    public void deleteContact(DeleteContactRequest request) {
        log.info("message=\"Calling deleteContact service\", correlation_id=\"{}\"", getCorrelationId());

        userService.getUserByName(request.getUsername())
                .flatMap(user -> validateAddressBookExists(request.getAddressId(), user.getId(), Arrays.asList(request.getContactId())))
                .subscribe(value -> contactService.delete(request.getContactId()));
    }

    private Mono<Optional<AddressBook>> validateAddressBookExists(Long addressId, Long userId, List<Long> contacts) {
        return Mono.just(addressBookRepository.findByIdAndUserIdAndContactsIn(addressId, userId, contacts))
                .filterWhen(addressBook -> Mono.just(addressBook.isPresent()))
                .switchIfEmpty(Mono.error(new ServiceException("Invalid address book")));
    }

    private Mono<Optional<AddressBook>> validateAddressBookExists(Long addressId, Long userId) {
        return Mono.just(addressBookRepository.findByIdAndUserId(addressId, userId))
                .filterWhen(addressBook -> Mono.just(addressBook.isPresent()))
                .switchIfEmpty(Mono.error(new ServiceException("Invalid address book")));
    }

    private Mono<List<AddressBook>> validateAddressBookExists(Long userId) {
        return Mono.just(addressBookRepository.findByUserId(userId))
                .filterWhen(addressBooks -> Mono.just(addressBooks.size() > 0))
                .switchIfEmpty(Mono.error(new ServiceException("No Address book exists")));
    }

    private Mono<Optional<AddressBook>> validateAddressBookNotExists(CreateAddressBookRequest request) {
        return Mono.just(addressBookRepository.findByNameAndUserName(request.getAddressBookName(), request.getUsername()))
                .filterWhen(addressBook -> Mono.just(!addressBook.isPresent()))
                .switchIfEmpty(Mono.error(new ServiceException("This address book exists")));
    }
}
