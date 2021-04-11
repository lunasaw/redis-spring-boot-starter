package com.luna.redis.util;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author luna@mac
 * 2021年04月11日 13:28
 */
@Component
public class RedisGeoUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 将指定成员名的点添加到键。
     * 
     * @param key
     * @param x 纬度
     * @param y 经度
     * @param name 名称
     */
    public void add(String key, String x, String y, String name) {
        redisTemplate.opsForGeo().add(key, new Point(Double.parseDouble(x), Double.parseDouble(y)), name);
    }

    /**
     * 批量添加
     * 
     * @param key
     * @param maps <String, Point>
     */
    public void addMap(String key, Map<String, Point> maps) {
        redisTemplate.opsForGeo().add(key, new HashMap<>(maps));
    }

    public void add(String key, String x, String y, Object name) {
        redisTemplate.opsForGeo().add(key,
            new RedisGeoCommands.GeoLocation<>(name, new Point(Double.parseDouble(x), Double.parseDouble(y))));
    }

    public void addObject(String key, Map<Object, Point> maps) {
        ArrayList<RedisGeoCommands.GeoLocation<Object>> list = Lists.newArrayList();
        maps.entrySet().stream().forEach(map -> {
            list.add(new RedisGeoCommands.GeoLocation<>(map.getKey(), map.getValue()));
        });

        redisTemplate.opsForGeo().add(key, list);
    }

    /**
     * 获取两地的直线距离
     * 
     * @param key
     * @param remember1 地点1
     * @param remember2 地点2
     * @param metrics 单位
     */
    public Distance distance(String key, Object remember1, Object remember2, Metrics metrics) {
        return redisTemplate.opsForGeo().distance(key, remember1, remember2, metrics);
    }

    /**
     * 获取两地的直线距离 默认使用KM
     * 
     * @param key
     * @param remember1
     * @param remember2
     * @return
     */
    public Distance distance(String key, Object remember1, Object remember2) {
        return distance(key, remember1, remember2, Metrics.KILOMETERS);
    }

    /**
     * 获取一个或多个成员位置的Geohash表示。
     * 
     * @param key
     * @param members
     * @return
     */
    public List<String> hash(String key, Object... members) {
        return redisTemplate.opsForGeo().hash(key, members);
    }

    /**
     * 删除键对应的成员
     * 
     * @param key
     * @param members
     * @return
     */
    public Long remove(String key, String... members) {
        return redisTemplate.opsForGeo().remove(key, members);
    }

    /**
     * 获取一个或多个成员的位置的点表示。
     * 
     * @param key
     * @param members
     * @return
     */
    public List<Point> position(String key, Object... members) {
        return redisTemplate.opsForGeo().position(key, members);
    }

    /**
     * 获取在给定的圆的边界内的成员
     * 
     * @param key
     * @param x 纬度
     * @param y 经度
     * @param value 半径
     * @param metrics 单位
     */
    public List<Object> radius(String key, String x, String y, String value, Metrics metrics) {
        return radius(key, x, y, null, value, metrics, null, null);
    }

    /**
     * 带条件半径查找
     * 
     * @param key
     * @param x
     * @param y
     * @param value
     * @param metrics
     * @param geoRadiusCommandArgs 查找条件参数
     * @return
     */
    public List<Object> radius(String key, String x, String y, String value, Metrics metrics,
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs) {
        return radius(key, x, y, null, value, metrics, geoRadiusCommandArgs, null);
    }

    /**
     * 获取由成员坐标和给定的应用度量的半径定义的圆内的成员。就是获取成员半径范围内的成员
     * 
     * @param key
     * @param member
     * @param value
     * @param metrics
     */
    public List<Object> radius(String key, Object member, String value, Metrics metrics) {
        return radius(key, null, null, member, value, metrics, null, null);
    }

    /**
     * 获取成员坐标和给定半径所定义的圈内的成员。
     * 
     * @param key
     * @param member
     * @param radius
     * @return
     */
    public List<Object> radius(String key, Object member, double radius) {
        return radius(key, null, null, member, null, null, null, radius);
    }

    public List<Object> radius(String key, String x, String y, Object member, String value, Metrics metrics,
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs, Double radius) {
        Distance distance = new Distance(Double.parseDouble(value), metrics);
        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoLocationGeoResults = null;
        if (radius != null) {
            geoLocationGeoResults =
                redisTemplate.opsForGeo().radius(key, member, radius);
        } else if (x != null && y != null) {
            Point point = new Point(Double.parseDouble(x), Double.parseDouble(y));
            if (geoRadiusCommandArgs == null) {
                geoLocationGeoResults =
                    redisTemplate.opsForGeo().radius(key, new Circle(point, distance), geoRadiusCommandArgs);
            } else {
                geoLocationGeoResults =
                    redisTemplate.opsForGeo().radius(key, new Circle(point, distance));
            }
        } else if (member != null) {
            if (geoRadiusCommandArgs != null) {
                geoLocationGeoResults =
                    redisTemplate.opsForGeo().radius(key, member, distance, geoRadiusCommandArgs);
            } else {
                geoLocationGeoResults =
                    redisTemplate.opsForGeo().radius(key, member, distance);
            }
        }
        ArrayList<Object> list = Lists.newArrayList();
        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> geoLocationGeoResult : geoLocationGeoResults) {
            list.add(geoLocationGeoResult.getContent().getName());
        }
        return list;
    }
}
