package com.reece.addressbook.component.common;

import com.reece.addressbook.AddressBookApplication;
import com.reece.addressbook.AddressBookApplicationTests;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {AddressBookApplicationTests.class, AddressBookApplication.class})
@ActiveProfiles("test")
public @interface ComponentTest {

}
