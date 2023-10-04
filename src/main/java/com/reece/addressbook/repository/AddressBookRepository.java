package com.reece.addressbook.repository;

import com.reece.addressbook.entity.AddressBook;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {
    Optional<AddressBook> findByNameAndUserName(String name, String user);

    Optional<AddressBook> findByIdAndUserId(Long id, Long userId);
    List<AddressBook> findByUserId(Long userId);

    Optional<AddressBook> findByIdAndUserIdAndContactsIn(Long id, Long userId, List<Long> contacts);
    @EntityGraph(attributePaths = {"contacts", "user"})
    Optional<AddressBook> findWithContactsByIdAndUserIdAndContactsIn(Long id, Long userId, List<Long> contacts);
}
