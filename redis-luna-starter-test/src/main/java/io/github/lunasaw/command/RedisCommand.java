package io.github.lunasaw.command;

import io.github.lunasaw.domain.User;
import io.github.lunasaw.util.RedisKeyUtil;
import io.github.lunasaw.util.RedisValueUtil;
import io.github.lunasaw.util.cache.LocalCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author chenzhangyue
 * 2023/1/31
 */
@Component
@ShellComponent
@Slf4j
public class RedisCommand {

    @Autowired
    private RedisKeyUtil redisKeyUtil;

    @Autowired
    private RedisValueUtil redisValueUtil;

    @Autowired
    private LocalCacheUtil localCacheUtil;

    @ShellMethod("get key")
    public Set<String> get(String key) {
        Set<String> keysWithSet = redisKeyUtil.getKeysWithSet(key);
        return keysWithSet;
    }

    @ShellMethod("set key")
    public String set(String key, String value) {
        redisValueUtil.set(key, value);
        return "success";
    }

    @ShellMethod("Download the nuclear codes.")
    public String download(String fileName) {
        Object o = redisValueUtil.get(fileName);
        if (o == null) {
            return "download fail";
        }
        return "download " + o + " success";
    }

    @ShellMethod("Connect to the server.")
    public void connect(@ShellOption(defaultValue = "123") String user, @ShellOption(defaultValue = "123") String password) {
        redisValueUtil.set("download_status", true);
    }

    @ShellMethodAvailability("download")
    public Availability availabilityCheck() {
        boolean b = redisKeyUtil.hasKey("download_status");

        return b
                ? Availability.available()
                : Availability.unavailable("-- you are not connected");
    }

    @ShellMethod("local key")
    public String localGet(String key) {
        return localCacheUtil.get(key).toString();
    }

    @ShellMethod("local put")
    public String localPut(String key, Object value) {
        localCacheUtil.set(key, value);
        return "success";
    }
}
