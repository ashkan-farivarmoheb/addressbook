package com.reece.addressbook.service;

import com.reece.addressbook.entity.User;
import com.reece.addressbook.exception.ServiceException;
import com.reece.addressbook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.reece.addressbook.common.MdcUtil.getCorrelationId;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Mono<User> getUserByName(String name) {
        log.info("message=\"Calling getUserByName service\", correlation_id=\"{}\"", getCorrelationId());

        return Mono.just(userRepository.findByName(name))
                .filterWhen(addressBook -> Mono.just(addressBook.isPresent()))
                .switchIfEmpty(Mono.error(new ServiceException("Invalid user")))
                .doOnError(e -> log.error("message=\"Invalid user\", correlation_id=\"{}\", \"{}\"", getCorrelationId(), e))
                .flatMap(user -> Mono.just(user.get()));
    }
}
