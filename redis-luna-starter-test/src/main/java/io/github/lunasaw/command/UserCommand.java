package io.github.lunasaw.command;

import io.github.lunasaw.domain.User;
import io.github.lunasaw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;

/**
 * @author chenzhangyue
 * 2023/2/1
 */
@ShellComponent
@Component
public class UserCommand {

    @Autowired
    private UserService userService;

    @ShellMethod("get user")
    @Cacheable("userCache")
    public User getUser(@ShellOption(defaultValue = "123") Long userId) {
        return userService.getById(userId);
    }

    @ShellMethod("add user")
    @CachePut(value = "userCache", key = "#result.userId")
    public User addUser(@ShellOption(value = "admin") String userName,
                        @ShellOption(value = "admin") String passWord) {
        User user = User.builder().userName(userName).userPassword(passWord).build();
        return userService.addUser(user);
    }
}
