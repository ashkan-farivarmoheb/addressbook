package com.reece.addressbook.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.reece.addressbook.entity.PhoneType;
import com.reece.addressbook.validation.RegexUtils;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateContactResponse {
    private String firstname;
    private String lastname;
    private String company;
    @Pattern(regexp = RegexUtils.PHONE_NUMBER)
    private String phoneNumber;
    private PhoneType type;
    private String addressName;
    private String username;
}
