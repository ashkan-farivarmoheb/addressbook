package com.reece.addressbook.service;

import com.reece.addressbook.api.CreateContactRequest;
import com.reece.addressbook.api.CreateContactResponse;
import com.reece.addressbook.api.GetContactRequest;
import com.reece.addressbook.api.GetContactResponse;
import com.reece.addressbook.api.GetContactsResponse;
import com.reece.addressbook.repository.ContactRepository;
import com.reece.addressbook.service.mapper.ContactMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.reece.addressbook.common.MdcUtil.getCorrelationId;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMapper contactMapper;
    private final ContactRepository contactRepository;

    public Mono<CreateContactResponse> create(CreateContactRequest request) {
        log.info("message=\"Calling ContactService.create service\", correlation_id=\"{}\"", getCorrelationId());

        return Mono.just(contactMapper.toContact(request))
                .flatMap(contact -> Mono.just(contactRepository.save(contact)))
                .doOnError(e -> log.error("message=\"Couldn't save to db\", correlation_id=\"{}\", \"{}\"", getCorrelationId(), e))
                .flatMap(contact -> Mono.just(contactMapper.toCreateContactResponse(contact)));
    }

    public void delete(Long contractId) {
        log.info("message=\"Calling ContactService.delete service\", correlation_id=\"{}\"", getCorrelationId());

        contactRepository.deleteById(contractId);
    }

    public Mono<Page<GetContactsResponse>> getContacts(GetContactRequest request) {
        log.info("message=\"Calling ContactService.getContacts service\", correlation_id=\"{}\"", getCorrelationId());

        return Flux.fromIterable(contactRepository.findByAddressBookId(request.getAddressId(), request.getPageable()))
                .flatMap(contact -> Flux.just(contactMapper.toGetContactResponse(contact)))
                .collectList()
                .flatMap(list -> Mono.just(new HashSet<>(list)))
                .flatMap(contacts -> Mono.just(getContactsResponse(request, contacts)))
                .zipWith(Mono.just(contactRepository.countByAddressBookId(request.getAddressId())))
                .flatMap(response -> Mono.just(new PageImpl<>(Arrays.asList(response.getT1()), request.getPageable(), response.getT2())));
    }

    public Mono<Page<GetContactsResponse>> getAllContacts(GetContactRequest request) {

        log.info("message=\"Calling ContactService.getAllContacts service\", correlation_id=\"{}\"", getCorrelationId());

        return Flux.fromIterable(contactRepository.findByAddressBookUserName(request.getUsername(), request.getPageable()))
                .flatMap(contact -> Flux.just(contactMapper.toGetContactResponse(contact)))
                .collectList()
                .flatMap(list -> Mono.just(new HashSet<>(list)))
                .flatMap(contacts -> Mono.just(getContactsResponse(request, contacts)))
                .zipWith(Mono.just(contactRepository.countByAddressBookUserName(request.getUsername())))
                .flatMap(response -> Mono.just(new PageImpl<>(Arrays.asList(response.getT1()), request.getPageable(), response.getT2())));
    }

    private static GetContactsResponse getContactsResponse(GetContactRequest request, Set<GetContactResponse> contacts) {
        return GetContactsResponse.builder()
                .addressId(request.getAddressId()).username(request.getUsername()).contacts(contacts)
                .build();
    }
}
