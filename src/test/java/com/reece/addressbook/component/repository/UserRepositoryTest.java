package com.reece.addressbook.component.repository;

import com.reece.addressbook.component.common.ComponentTest;
import com.reece.addressbook.entity.User;
import com.reece.addressbook.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ComponentTest
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = {"/db/scripts/user/cleanup.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"/db/scripts/user/data.sql"})
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByName_validData_shouldSuccess() {
        Optional<User> user = userRepository.findByName("ashkan");

        assertNotNull(user);
        assertEquals(user.get().getName(), "ashkan");
    }

    @Test
    void findByName_vinalidData_emptyResult() {
        Optional<User> user = userRepository.findByName("ashkan1");

        Assertions.assertThat(user).isEmpty();
    }
}
