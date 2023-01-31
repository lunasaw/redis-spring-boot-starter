package com.luna.redis.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author luna@mac
 * 2021年04月10日 19:52
 */
@Component
public class RedisValueUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public <T> T get(String key, TypeReference<T> typeReference) {
        return JSON.parseObject(JSON.toJSONString(get(key)), typeReference);
    }

    /**
     * 普通缓存获取 根据 key 获取对应的value 如果key不存在则返回null
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 移位操作 获取bit
     *
     * @param key
     * @param offset
     */
    public Boolean getBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 获取当前位上的值
     *
     * @param key
     * @param offset
     * @param value
     */
    public Boolean setBit(String key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 计数
     * 
     * @param key
     * @return
     */
    public Long size(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    /**
     * 获取key 值从 start位置开始到end位置结束。 等于String 的 subString 前后闭区间
     * 0 -1 整个key的值
     * -4 -1 从尾部开始往前截长度为4
     *
     * @param key 不能为null
     * @param start 起始位置
     * @param end 结束位置
     * @see <a href="http://redis.io/commands/getrange">Redis Documentation: GETRANGE</a>
     */
    public String get(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 设置key的值为value 并返回旧值。 如果key不存在返回为null
     *
     * @param key 不能为null
     */
    public Object getAndSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 普通缓存放入
     * 如果key不存在添加key 保存值为value
     * 如果key存在则对value进行覆盖
     * 
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将value从指定的位置开始覆盖原有的值。如果指定的开始位置大于字符串长度，先补空格在追加。
     * 如果key不存在，则等于新增。长度大于0则先补空格 set("key10", "abc", 3) 得到结果为：
     * 3空格 +"abc"
     *
     * @param key 不能为null
     * @param value 值
     * @param offset 开始的位置
     */
    public void set(String key, Object value, long offset) {
        redisTemplate.opsForValue().set(key, value, offset);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    /**
     * 为 key的值末尾追加 value 如果key不存在就直接等于 set(K key, V value)
     *
     * @param key 不能为null
     * @param value 追加的值
     * @see <a href="http://redis.io/commands/append">Redis Documentation: APPEND</a>
     */
    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    /**
     * 如果key不存在，则设置key 的值为 value. 存在则不设置
     * 设置成功返回true 失败返回false
     *
     * @param key   key不能为空
     * @param value 设置的值
     */
    public Boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 把一个map的键值对添加到redis中，key-value 对应着 key value。如果key已经存在就覆盖，
     * 
     * @param map 不能为null 为null抛出空指针异常 可以为空集合
     */
    public void multiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 把一个map的键值对添加到redis中，key-value 对应着 key value。 当且仅当map中的所有key都
     * 不存在的时候，添加成功返回 true，否则返回false.
     *
     * @param map map不能为空 可以为empty
     */
    public Boolean multiSetIfAbsent(Map<String, Object> map) {
        return redisTemplate.opsForValue().multiSetIfAbsent(map);
    }

    /**
     * 根据提供的key集合按顺序获取对应的value值
     * 
     * @param keys 集合不能为null 可以为empty 集合
     */
    public List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 递增 为key 的值加上 long delta. 原来的值必须是能转换成Integer类型的。否则会抛出异常。
     *
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 为key 的值加上 double delta. 原来的值必须是能转换成Integer类型的。否则会抛出异常。
     * 添加double后不能再加整数。已经无法在转换为Integer
     *
     * @param key 不能为null
     * @param delta 增加的值
     */
    public Double increment(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }
}
