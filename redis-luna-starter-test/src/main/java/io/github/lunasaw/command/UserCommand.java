package io.github.lunasaw.command;

import io.github.lunasaw.domain.User;
import io.github.lunasaw.service.UserService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
    public User addUser(@ShellOption(value = "-u") String userName,
                        @ShellOption(value = "-p") String passWord) {
        User user = User.builder().userName(userName).userPassword(passWord).userId(RandomUtils.nextLong()).build();
        return userService.addUser(user);
    }

    @ShellMethod("del user")
    @CacheEvict(value = "userCache", key = "#userId", allEntries = false, beforeInvocation = true)
    public Boolean delUser(@ShellOption(defaultValue = "123") Long userId) {
        return userService.delUser(userId);
    }
}
