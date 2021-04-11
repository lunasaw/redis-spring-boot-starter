package com.luna.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author luna@mac
 * 2021年04月11日 20:18
 */
@Component
public class RedisStreamUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

}
