package com.reece.addressbook.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reece.addressbook.entity.PhoneType;
import com.reece.addressbook.validation.RegexUtils;
import jakarta.validation.constraints.NotBlank;
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
public class CreateContactRequest {
    @NotBlank(message = "Firstname could not be empty")
    private String firstname;
    private String lastname;
    private String company;
    @NotBlank(message = "PhoneNumber could not be empty")
    @Pattern(regexp = RegexUtils.PHONE_NUMBER)
    private String phoneNumber;
    private PhoneType type;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    private Long addressId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    private String username;
}
