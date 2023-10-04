package com.reece.addressbook.unit.service;

import com.reece.addressbook.entity.User;
import com.reece.addressbook.repository.UserRepository;
import com.reece.addressbook.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void getUserByName_validInput_returnUser() {
        when(userRepository.findByName(anyString()))
                .thenReturn(Optional.ofNullable(User.builder().id(1l).name("user1").build()));
        StepVerifier.create(userService.getUserByName("user1"))
                .thenConsumeWhile(fetchedObject -> {
                    assertNotNull(fetchedObject);
                    assertNotNull(fetchedObject.getId());
                    Assertions.assertEquals(fetchedObject.getName(), "user1");
                    return true;
                }).verifyComplete();
    }
}
