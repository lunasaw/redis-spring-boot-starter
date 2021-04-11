package com.luna.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author luna@mac
 * 2021年04月11日 16:48
 */
@Component
public class RedisHyperlogUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 添加元素
     * 
     * @param key
     * @param values
     */
    public Long add(String key, String... values) {
        return redisTemplate.opsForHyperLogLog().add(key, values);
    }

    /**
     * 删除整个键值对
     * 
     * @param key
     */
    public void delete(String key) {
        redisTemplate.opsForHyperLogLog().delete(key);
    }

    /**
     * 统计多个键值之间的不重复个数 有误差 0.81%
     * 
     * @param keys
     */
    public Long size(String... keys) {
        return redisTemplate.opsForHyperLogLog().size(keys);
    }

    /**
     * 将给定sourceKeys的所有值合并为目标键。
     * 
     * @param destination 目标存储键
     * @param sourceKeys
     * @return
     */
    public Long union(String destination, String... sourceKeys) {
        return redisTemplate.opsForHyperLogLog().union(destination, sourceKeys);
    }
}
