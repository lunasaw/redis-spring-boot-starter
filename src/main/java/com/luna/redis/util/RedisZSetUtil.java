package com.luna.redis.util;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

/**
 * @author luna@mac
 * 2021年04月10日 16:21
 */
@Component
public class RedisZSetUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 将数据放入zset缓存
     *
     * @param key    键
     * @param values 值
     * @return 成功个数
     */
    public boolean add(String key, Object values, Double score) {
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, values, score));
    }

    /**
     * 多元素添加
     *
     * @param key
     * @param maps Map<数据, 权重>
     */
    public void add(String key, Map<Object, Double> maps) {
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = maps.entrySet().stream()
                .map(e -> new DefaultTypedTuple<>(e.getKey(), e.getValue())).collect(Collectors.toSet());
        redisTemplate.opsForZSet().add(key, typedTuples);
    }

    /**
     * 匹配获取键值对，ScanOptions.NONE为获取全部键值对；ScanOptions.scanOptions().match("C").build()匹配获取键位map1的键值对,不能模糊匹配。
     *
     * @param key
     * @param count
     * @param pattern
     * @return
     */
    public Map<Object, Double> scan(String key, Long count, String pattern) {
        ScanOptions match = ScanOptions.scanOptions().count(count).match(pattern).build();
        Cursor<ZSetOperations.TypedTuple<Object>> cursor = redisTemplate.opsForZSet().scan(key, match);

        Map<Object, Double> dataMap = cursor.stream().collect(
                Collectors.toMap(ZSetOperations.TypedTuple::getValue, e -> Optional.of(e).map(ZSetOperations.TypedTuple::getScore).orElse(0.0)));

        if (MapUtils.isEmpty(dataMap)) {
            return Maps.newHashMap();
        }
        return dataMap;
    }

    /**
     * Count排序集合中分数在min和max之间的元素个数。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long count(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }


    /**
     * 用于获取满足非score的排序取值。
     * 这个排序只有在有相同分数的情况下才能使用，如果有不同的分数则返回值不确定。
     *
     * @param key
     * @param range
     */
    public Set<Object> rangeByLex(String key, RedisZSetCommands.Range range) {
        return redisTemplate.opsForZSet().rangeByLex(key, range);
    }

    /**
     * 根据设置的score获取区间值。
     *
     * @param key
     * @param min
     * @param max
     */
    public Set<Object> rangeByScore(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 索引倒序排列区间值。
     *
     * @param key
     * @param start
     * @param end
     */
    public Map<Object, Double> reverseRangeWithScores(String key, long start, long end) {
        Set<ZSetOperations.TypedTuple<Object>> typedTupleSet =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);

        Map<Object, Double> dataMap = typedTupleSet.stream().collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore));

        return dataMap;
    }

    /**
     * 获取倒序排列的索引值。
     *
     * @param key
     * @param value
     */
    public Long reverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取变量中元素的索引,下标开始位置为0。
     *
     * @param key
     * @param value
     */
    public Long rank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 修改元素的分值
     *
     * @param key
     * @param value
     * @param delta
     */
    public Double incrementSort(String key, Object value, Double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 正序获取RedisZSetCommands.Tuples的区间值通过分值。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Map<Object, Double> rangeByScoreWithScores(String key, Double min, Double max) {
        Set<ZSetOperations.TypedTuple<Object>> typedTupleSet =
                redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);

        Map<Object, Double> dataMap = typedTupleSet.stream().collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore));

        return dataMap;
    }

    /**
     * 倒序排序获取RedisZSetCommands.Tuples的分值区间值。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Map<Object, Double> reverseRangeByScoreWithScores(String key, Double min, Double max) {
        return reverseRangeByScoreWithScores(key, min, max, null, null);
    }

    public Map<Object, Double> reverseRangeByScoreWithScores(String key, Double min, Double max, Long offset,
                                                             Long count) {
        Set<ZSetOperations.TypedTuple<Object>> typedTupleSet = null;
        if (offset != null && count != null) {
            typedTupleSet = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max, offset, count);
        } else {
            typedTupleSet = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
        }
        Map<Object, Double> dataMap = typedTupleSet.stream().collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore));

        return dataMap;
    }

    /**
     * 在key和otherKeys中交叉排序集，并将结果存储在目标destKey中。
     *
     * @param key
     * @param otherKey
     * @param destKey
     */
    public Long intersectAndStore(String key, String otherKey, String destKey) {
        return intersectAndStore(key, Lists.newArrayList(otherKey), destKey, null, null);
    }

    public Long intersectAndStore(String key, List<String> otherKeys, String destKey) {
        return intersectAndStore(key, otherKeys, destKey, null, null);
    }

    public Long intersectAndStore(String key, String otherKey, String destKey, RedisZSetCommands.Aggregate aggregate) {
        return intersectAndStore(key, Lists.newArrayList(otherKey), destKey, aggregate, null);
    }

    public Long intersectAndStore(String key, List<String> otherKeys, String destKey,
                                  RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
        ArrayList<String> list = Lists.newArrayList(otherKeys);
        if (otherKeys.size() == 1) {
            return redisTemplate.opsForZSet().intersectAndStore(key, list.get(0), destKey);
        } else if (aggregate != null && weights != null) {
            return redisTemplate.opsForZSet().intersectAndStore(key, list, destKey, aggregate, weights);
        } else if (aggregate != null) {
            return redisTemplate.opsForZSet().intersectAndStore(key, list, destKey, aggregate);
        } else {
            return redisTemplate.opsForZSet().intersectAndStore(key, list, destKey);
        }
    }

    /**
     * 获取元素的分值。
     *
     * @param key
     * @param value
     * @return
     */
    public Double score(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 获取变量中元素的个数
     *
     * @param key
     */
    public Long count(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 求指定集合与另一个集合的并集，并保存到目标集合
     *
     * @param key      集合
     * @param otherKey 另一个集合
     * @param destKey  目标集合
     * @return
     */
    public Long unionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * 求指定集合与另外多个集合的并集，并保存到目标集合
     *
     * @param key      集合
     * @param otherKey 另外多个集合
     * @param destKey  目标集合
     * @return
     */
    public Long unionAndStore(String key, Collection<String> otherKey, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * 移除元素
     *
     * @param key
     * @param values
     */
    public Long remove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 根据区间删除
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long removeRangeByScore(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * 根据索引值移除区间元素。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long removeRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }
}
