package io.github.lunasaw.service;

import io.github.lunasaw.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author chenzhangyue
 * 2023/2/1
 */
@Component
@Slf4j
public class UserService {

    public User getById(Long userId) {
        log.info("no cache this getById::userId = {}", userId);
        return User.builder().userId(userId).build();
    }

    public User addUser(User user) {
        log.info("addUser::user = {}", user);
        return user;
    }
}
