package io.github.lunasaw.service;

import com.alibaba.fastjson.TypeReference;
import com.google.common.cache.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.lunasaw.domain.User;
import io.github.lunasaw.util.CacheQueryUtils;
import io.github.lunasaw.util.RedisHashUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chenzhangyue
 * 2023/2/1
 */
@Component
@Slf4j
public class UserService {

    private static final String USER_LIST = "user_list";
    @Autowired
    private RedisHashUtil redisHashUtil;

    @Value("${cache.default.expire-time}")
    private int defaultExpireTime;

    LoadingCache<String, User> loadingCache = CacheBuilder.newBuilder()
            .maximumSize(100L)
            .expireAfterWrite(defaultExpireTime, TimeUnit.MINUTES)
            .removalListener(notification -> log.info("onRemoval::notification = {}", notification))
            .build(new CacheLoader<String, User>() {
                public User load(@NotNull String key) {
                    Map<String, User> userMap = batchQuery(Collections.singletonList(key));
                    return userMap.get(key);
                }
            });

    @SneakyThrows
    public Map<String, User> getUserByLocalCache(List<String> userId) {
        ImmutableMap<String, User> all = loadingCache.getAll(userId);
        return Maps.newHashMap(all);
    }

    public Map<String, User> batchQuery(List<String> userIds) {
        CacheQueryUtils.Req<String, User> req = new CacheQueryUtils.Req<>();
        List<String> strUserIds = userIds.stream().map(String::valueOf).collect(Collectors.toList());
        req.setKeys(strUserIds);
        req.setNamespace(() -> USER_LIST);
        req.setExpiredTime(defaultExpireTime);

        req.setSql(keys -> batchQueryUserIds(strUserIds));
        req.setTypeReference(() -> new TypeReference<User>() {
        });
        req.setKeyGenerate(value -> Optional.ofNullable(value).map(User::getUserId).map(String::valueOf).orElse(null));
        req.setValidate(t -> (t != null && t.getUserId() != null));
        req.setMock(User::new);

        Map<String, User> map = CacheQueryUtils.batchQuery(redisHashUtil, req, true);

        return map;
    }

    public User getById(Long userId) {
        log.info("no cache this getById::userId = {}", userId);
        return User.builder().userId(userId).build();
    }

    public User addUser(User user) {
        log.info("addUser::user = {}", user);
        redisHashUtil.put(USER_LIST, user.getUserId().toString(), user);
        return user;
    }

    public Boolean delUser(Long userId) {
        if (userId == null) {
            return true;
        }
        log.info("delUser::userId = {}", userId);
        redisHashUtil.delete(USER_LIST, userId);
        loadingCache.invalidate(userId.toString());
        return true;
    }

    public List<User> batchQueryUserIds(List<String> userIds) {
        log.info("getList::userIds = {}", userIds);
        List<User> users = redisHashUtil.multiGet(USER_LIST, Sets.newHashSet(userIds), new TypeReference<User>() {
        });
        return users;
    }
}
