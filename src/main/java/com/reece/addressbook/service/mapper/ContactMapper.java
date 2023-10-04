package com.reece.addressbook.service.mapper;

import com.reece.addressbook.api.CreateContactRequest;
import com.reece.addressbook.api.CreateContactResponse;
import com.reece.addressbook.api.GetContactResponse;
import com.reece.addressbook.entity.Contact;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        builder = @Builder(disableBuilder = true),
        imports = {Optional.class})
public interface ContactMapper {

    @Mapping(target = "addressBook.id", source = "request.addressId")
    @Mapping(target = "number", source = "phoneNumber")
    @Mapping(target = "id", ignore = true)
    Contact toContact(CreateContactRequest request);
    @Mapping(target = "addressName", source = "addressBook.name")
    @Mapping(target = "username", source = "addressBook.user.name")
    @Mapping(target = "phoneNumber", source = "number")
    CreateContactResponse toCreateContactResponse(Contact contact);

    @Mapping(target = "phoneNumber", source = "number")
    @Mapping(target = "addressName", source = "addressBook.name")
    GetContactResponse toGetContactResponse(Contact contact);
}
