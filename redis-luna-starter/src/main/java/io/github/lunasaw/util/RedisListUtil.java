package io.github.lunasaw.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author luna@mac
 * 2021年04月10日 16:21
 */
@Component
@Slf4j
public class RedisListUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisKeyUtil redisKeyUtil;

    public <T> T getRange(String key, long start, long end, TypeReference<T> typeReference) {
        List<Object> range = getRange(key, start, end);
        return JSON.parseObject(JSON.toJSONString(range), typeReference);
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1 代表所有值
     * @return
     */
    public List<Object> getRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 范围获取
     *
     * @param key
     * @return
     */
    public <T> T getRange(String key, TypeReference<T> typeReference) {
        List<Object> range = getRange(key, 0, -1);
        return JSON.parseObject(JSON.toJSONString(range), typeReference);
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public Long getSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public <T> T getIndex(String key, long index, TypeReference<T> typeReference) {
        Object object = redisTemplate.opsForList().index(key, index);
        return JSON.parseObject(JSON.toJSONString(object), typeReference);
    }
    // ===============================list左侧弹出放到其他key的右侧================================

    /**
     * 从sourceKey的列表中删除最后一个元素，将其添加到destinationKey并返回其值。
     */
    public Object rightPopAndLeftPush(String sourceKey, String destinationKey) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
    }

    /**
     * 从srcKey列表中删除最后一个元素，将其添加到dstKey并返回其值。阻塞连接，直到达到可用元素或超时。
     *
     * @param sourceKey
     * @param destinationKey
     * @param time
     * @param timeUnit
     * @return
     */
    public Object rightPopAndLeftPush(String sourceKey, String destinationKey, long time, TimeUnit timeUnit) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, time, timeUnit);
    }

    // ===============================list左侧弹出================================

    /**
     * 左侧弹出
     *
     * @param key
     * @return
     */
    public Object leftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 从存储在key的列表中移除并返回第一个元素。阻塞连接，直到达到可用元素或超时。
     */
    public Object leftPop(String key, long time, TimeUnit timeUnit) {
        return redisTemplate.opsForList().leftPop(key, time, timeUnit);
    }

    // ===============================list右侧弹出================================

    /**
     * 右侧弹出
     *
     * @param key
     * @return
     */
    public Object rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 从存储在key的列表中移除并返回第一个元素。阻塞连接，直到达到可用元素或超时。
     */
    public Object rightPop(String key, long time, TimeUnit timeUnit) {
        return redisTemplate.opsForList().rightPop(key, time, timeUnit);
    }

    // ===============================list右侧放入=================================

    /**
     * 右侧放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public Long rightSet(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 带时间右侧放入缓存
     *
     * @param key      键
     * @param value    值
     * @param time     时间(秒)
     * @param timeUnit 单位 默认秒
     * @return
     */
    public boolean rightSet(String key, Object value, long time, TimeUnit timeUnit) {
        if (1 != rightSet(key, value)) {
            return false;
        }
        return redisKeyUtil.expire(key, time, timeUnit);
    }

    /**
     * 右侧List放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public Long rightSetAll(String key, List<Object> value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 带时间右侧放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean rightPushAll(String key, List<Object> value, long time, TimeUnit timeUnit) {
        if (value.size() != rightSetAll(key, value)) {
            return false;
        }
        return redisKeyUtil.expire(key, time, timeUnit);
    }

    // ===============================list左侧放入=================================

    /**
     * 左侧放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public Long leftSet(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 带时间左侧放入缓存
     *
     * @param key      键
     * @param value    值
     * @param time     时间(秒)
     * @param timeUnit 单位 默认秒
     * @return
     */
    public boolean leftSet(String key, Object value, long time, TimeUnit timeUnit) {
        if (1 != leftSet(key, value)) {
            return false;
        }
        return redisKeyUtil.expire(key, time, timeUnit);
    }

    /**
     * 右侧List放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public Long leftSetAll(String key, List<Object> value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 带时间右侧放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean leftPushAll(String key, List<Object> value, long time, TimeUnit timeUnit) {
        if (value.size() != leftSetAll(key, value)) {
            return false;
        }
        return redisKeyUtil.expire(key, time, timeUnit);
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public void update(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个 最多 getSize(key) 个
     * @param value 值
     * @return 移除的个数
     */
    public Long remove(String key, long count, Object value) {
        if (getSize(key) > count) {
            count = getSize(key);
        }
        return redisTemplate.opsForList().remove(key, count, value);
    }
}
