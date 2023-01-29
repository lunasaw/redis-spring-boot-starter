package com.luna.redis.util;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author luna@mac
 * 2021年04月11日 20:18
 */
@Component
public class RedisStreamUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 确认已处理的一个或多个记录。
     * 
     * @param key
     * @param group
     * @param recordIds
     * @return
     */
    public Long acknowledge(String key, String group, String... recordIds) {
        return redisTemplate.opsForStream().acknowledge(key, group, recordIds);
    }

    public Long acknowledge(String key, String group, RecordId... recordIds) {
        return redisTemplate.opsForStream().acknowledge(key, group, recordIds);
    }

    /**
     * 确认给定记录已处理。
     * 
     * @param key
     * @param group
     * @param value
     * @param recordId
     * @return
     */
    public Long acknowledge(String key, String group, Object value, String recordId) {
        Record record =
            StreamRecords.objectBacked(value).withId(RecordId.of(recordId)).withStreamKey(key);
        return redisTemplate.opsForStream().acknowledge(group, record);
    }

    /**
     * 将记录追加到流键。
     * 
     * @param key
     * @param content
     * @return
     */
    public RecordId add(String key, Map<String, Object> content) {
        return redisTemplate.opsForStream().add(key, content);
    }

    public RecordId add(String key, Object value, String recordId) {
        Record record =
            StreamRecords.objectBacked(value).withId(RecordId.of(recordId)).withStreamKey(key);
        return redisTemplate.opsForStream().add(record);
    }

    public Long delete(String key, String... recordIds) {
        return redisTemplate.opsForStream().delete(key, recordIds);
    }

    public Long delete(String key, RecordId... recordIds) {
        return redisTemplate.opsForStream().delete(key, recordIds);
    }

    public Long method(Record record) {
        return redisTemplate.opsForStream().delete(record);
    }

    /**
     * 从流中删除给定的记录。
     * 
     * @param key
     * @param recordIds
     */
    public Long consumers(String key, String... recordIds) {
        return redisTemplate.opsForStream().delete(key, recordIds);
    }

    public Long consumers(String key, RecordId... recordIds) {
        return redisTemplate.opsForStream().delete(key, recordIds);
    }

    /**
     * 从用户组中删除用户。
     *
     * @param key
     * @param group
     * @param name
     */
    public Boolean deleteConsumer(String key, String group, String name) {
        return redisTemplate.opsForStream().deleteConsumer(key, Consumer.from(group, name));
    }

    /**
     * 摧毁一个消费群体。
     *
     * @param key
     * @param group
     * @return
     */
    public Boolean destroyGroup(String key, String group) {
        return redisTemplate.opsForStream().destroyGroup(key, group);
    }

    /**
     * 创建用户组。如果流不存在，则此命令创建流。
     * 
     * @param key
     * @param group
     * @param offset
     * @return
     */
    public String createGroup(String key, String group, String offset) {
        if (StringUtils.isEmpty(offset)) {
            return redisTemplate.opsForStream().createGroup(key, group);
        }
        return redisTemplate.opsForStream().createGroup(key, ReadOffset.from(offset), group);
    }

    public Long size(String key) {
        return redisTemplate.opsForStream().size(key);
    }
}
