package com.luna.redis.util;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * redisKey设计
 */
@Component
public class RedisKeyUtil {

    public static <K, HK> K getRealKey(K k, HK key) {
        return (K) (k.toString() + key.toString());
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Long execute(String script, List<String> keys, Object... args) {
        // 指定 lua 脚本，并且指定返回值类型
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
        Long execute = redisTemplate.execute(redisScript, keys, args);
        return execute;
    }

    /**
     * 移动key到指定DB
     *
     * @param key
     * @param dbIndex
     * @return
     */
    public boolean move(String key, final int dbIndex) {
        return Boolean.TRUE.equals(redisTemplate.move(key, dbIndex));
    }

    /**
     * 清除所选择数据库
     */
    public void flushDb() {
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory == null) {
            return;
        }
        RedisConnection connection = connectionFactory.getConnection();
        connection.flushDb();
    }

    /**
     * 清除所有数据库
     */
    public void flushAll() {
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory == null) {
            return;
        }
        RedisConnection connection = connectionFactory.getConnection();
        connection.flushAll();
    }

    /**
     * 指定缓存失效时间
     *
     * @param key      键
     * @param time     时间(秒)
     * @param timeUnit
     * @return
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        if (timeUnit != null) {
            return Boolean.TRUE.equals(redisTemplate.expire(key, time, timeUnit));
        }
        return Boolean.TRUE.equals(redisTemplate.expire(key, time, TimeUnit.SECONDS));
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        return expire(key, time, null);
    }

    /**
     * 清空所有键
     */
    public void cleanAll() {
        Set<String> keys = redisTemplate.keys("*");
        delete(keys);
    }

    /**
     * 返回模糊匹配的key 不重复
     *
     * @param key Set<String>
     * @return
     */
    public Set<String> getKeysWithSet(String key) {
        return redisTemplate.keys(key);
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        if (timeUnit == null) {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        }
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key
     * @return
     */
    public long getExpire(String key) {
        return getExpire(key, null);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 返回集合中存在的key
     *
     * @param keys
     * @return
     */
    public Long countExistingKeys(Collection<String> keys) {
        return redisTemplate.countExistingKeys(keys);
    }

    /**
     * 将key设置为永久有效
     *
     * @param key
     */
    public boolean persistKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.persist(key));
    }

    /**
     * 删除key
     *
     * @param keys 可以传一个值 或多个
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 删除key
     *
     * @param keys 可以传一个值 或多个
     */
    public void delete(String... keys) {
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }

    /**
     * newKey不存在时才重命名
     *
     * @param oldKey
     * @param newKey
     * @return 修改成功返回true
     */
    public boolean renameKeyNotExist(String oldKey, String newKey) {
        return Boolean.TRUE.equals(redisTemplate.renameIfAbsent(oldKey, newKey));
    }

    /**
     * 删除Key的集合
     *
     * @param keys
     */
    public boolean deleteKey(Collection<String> keys) {
        Long delete = delete(keys);
        return Objects.equals((long) keys.size(), delete);
    }

    /**
     * redis的key
     * 可形式为：
     * 表名:主键名:主键值:列名
     *
     * @param split
     * @param key
     * @return
     */
    public static String getKey(String split, String... key) {
        StringBuilder builder = new StringBuilder();
        for (String s : key) {
            builder.append(s).append(split);
        }
        String s = builder.toString();
        return s.substring(0, s.length() - 1);
    }
}