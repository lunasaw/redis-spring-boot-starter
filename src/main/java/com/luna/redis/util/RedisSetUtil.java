package com.luna.redis.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author luna@mac
 * 2021年04月10日 16:21
 */
@Component
public class RedisSetUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisKeyUtil                  redisKeyUtil;

    /**
     * 获取集合中的所有元素
     *
     * @param key 键
     * @return
     */
    public Set<Object> getAll(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 随机获取集合中的一个元素
     * 
     * @param key
     * @return
     */
    public Object getRandom(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机返回集合中指定数量的元素。随机的元素不会重复
     * 
     * @param key
     * @param count
     * @return
     */
    public Set<Object> distinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * 随机返回集合中指定数量的元素。随机的元素可能重复
     * 
     * @param key
     * @param count
     * @return
     */
    public List<Object> randomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public Long getSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 将数据放入set缓存
     *
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public boolean set(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values) == values.length;
    }

    /**
     * 将set数据放入缓存
     *
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public boolean set(String key, long time, TimeUnit timeUnit, Object... values) {
        if (!set(key, values)) {
            return false;
        }
        return redisKeyUtil.expire(key, time, timeUnit);
    }

    /**
     * 移除值为value的
     *
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public boolean remove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values) == values.length;
    }

    /**
     * 求指定集合与另一个集合的并集 并返回并集
     *
     * @param key 不能为null
     * @param otherKey 不能为null
     */
    public Set<Object> union(String key, String otherKey) {
        return redisTemplate.opsForSet().union(key, otherKey);
    }

    /**
     * 求指定集合与
     * <p>
     * 另外多个
     * <p/>
     * 集合的并集 并返回并集
     *
     * @param key 不能为null
     * @param otherKey 不能为null
     */
    public Set<Object> union(String key, Collection<String> otherKey) {
        return redisTemplate.opsForSet().union(key, otherKey);
    }

    /**
     * 求指定集合与另一个集合的并集，并保存到目标集合
     * 
     * @param key 集合
     * @param otherKey 另一个集合
     * @param destKey 目标集合
     * @return
     */
    public Long unionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * 求指定集合与另外多个集合的并集，并保存到目标集合
     * 
     * @param key 集合
     * @param otherKey 另外多个集合
     * @param destKey 目标集合
     * @return
     */
    public Long unionAndStore(String key, Collection<String> otherKey, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * 求指定集合与另一个集合的差集
     */
    public Set<Object> difference(String key, String otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * 求指定集合与另外多个集合的差集
     * 
     * @param key 集合
     * @param otherKey 另外多个集合
     * @return
     */
    public Set<Object> difference(String key, Collection<String> otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * 求指定集合与另一个集合的差集，并保存到目标集合
     * 
     * @param key 集合
     * @param otherKey 另外多个集合
     * @param destKey 目标集合
     * @return
     */
    public Long differenceAndStore(String key, Collection<String> otherKey, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
    }

    /**
     * 求指定集合与另一个集合的差集，并保存到目标集合
     * 
     * @param key 集合
     * @param otherKey 另外多个集合
     * @param destKey 目标集合
     * @return
     */
    public Long differenceAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
    }
}
