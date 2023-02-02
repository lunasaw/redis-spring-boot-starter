package io.github.lunasaw.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author luna@mac
 * 2021年04月10日 19:44
 */
public class RedisHashUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisKeyUtil redisKeyUtil;

    public <K, HK, T> T get(K key, HK item, TypeReference<T> typeReference) {
        return JSON.parseObject(JSON.toJSONString(get(key, item)), typeReference);
    }

    public <T, HK> List<T> multiGet(String key, Set<HK> item, TypeReference<T> typeReference) {
        List<Object> list = multiGet(key, item);
        ArrayList<@Nullable T> arrayList = Lists.newArrayListWithCapacity(item.size());
        for (Object e : list) {
            if (e == null) {
                continue;
            }
            T t = JSON.parseObject(JSON.toJSONString(e), typeReference);
            arrayList.add(t);
        }
        return arrayList;
    }

    public <K, HK, T> HashMap<K, T> multiGetForOne(K key, Set<HK> item, TypeReference<T> typeReference, T defaultValue) {
        HashMap<K, T> kvHashMap = Maps.newHashMapWithExpectedSize(item.size());
        item.forEach(e -> {
            boolean hasKey = hasKey(key, e);
            K realKey = RedisKeyUtil.getRealKey(key, e);
            if (hasKey) {
                T value = get(key, e, typeReference);
                kvHashMap.putIfAbsent(realKey, value);
            } else {
                kvHashMap.putIfAbsent(realKey, defaultValue);
            }
        });

        return kvHashMap;
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public <K, HK> List<Object> multiGet(K key, Set<HK> item) {
        return redisTemplate.opsForHash().multiGet(key.toString(), Lists.newArrayList(item));
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public <K, HK> Object get(K key, HK item) {
        return redisTemplate.opsForHash().get(key.toString(), item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> getAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public <K, T> void set(String key, Map<K, T> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public <K, T> boolean set(String key, Map<K, T> map, long time) {
        set(key, map);
        return redisKeyUtil.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public <K, T> boolean set(String key, Map<K, T> map, long time, TimeUnit timeUnit) {
        set(key, map);
        return redisKeyUtil.expire(key, time, timeUnit);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public void put(String key, String item, Object value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean put(String key, String item, Object value, long time, TimeUnit timeUnit) {
        put(key, item, value);
        return redisKeyUtil.expire(key, time, timeUnit);
    }

    /**
     * 删除hash表中的值
     *
     * @param key      键 不能为null
     * @param hashKeys 项 可以使多个 不能为null
     */
    public void delete(String key, Object... hashKeys) {
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public <K, HK> boolean hasKey(K key, HK item) {
        return redisTemplate.opsForHash().hasKey(key.toString(), item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double increment(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double decrement(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

}
