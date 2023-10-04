package com.reece.addressbook.service.mapper;

import com.reece.addressbook.api.CreateAddressBookRequest;
import com.reece.addressbook.api.CreateAddressBookResponse;
import com.reece.addressbook.entity.AddressBook;
import com.reece.addressbook.entity.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        builder = @Builder(disableBuilder = true),
        imports = {Optional.class})
public interface AddressBookMapper {

    @Mapping(target = "name", source = "request.addressBookName")
    @Mapping(target = "user.id", source = "user.id")
    @Mapping(target = "user.name", source = "user.name")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    AddressBook toAddressBook(CreateAddressBookRequest request, User user);

    @Mapping(target = "user", source = "user.name")
    CreateAddressBookResponse toCreateAddressBookResponse(AddressBook addressBook);
}
