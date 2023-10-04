package com.reece.addressbook.repository;

import com.reece.addressbook.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Page<Contact> findByAddressBookId(Long addressId, Pageable pageable);
    Page<Contact> findByAddressBookUserName(String username, Pageable pageable);
    long countByAddressBookId(Long addressId);
    long countByAddressBookUserName(String username);
}
